package com.influxdata.PickACard;

import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DeckOfCardService {

  private final WebClient webclient;
  private final Counter cardDrawCounter;

  public DeckOfCardService(WebClient.Builder webClientBuilder, MeterRegistry meterRegistry) {
    this.webclient = webClientBuilder.baseUrl("https://deckofcardsapi.com/api").build();
    this.cardDrawCounter = Counter.builder("cards.drawn")
                                  .description("Number of cards drawn")
                                  .register(meterRegistry);
  }

  public Mono<String> getRandomCardSvg() {
    return webclient.get()
                    .uri("/deck/new/draw/?count=1")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(jsonNode -> jsonNode.get("cards").get(0).get("image").asText())
                    .doOnSuccess(s -> cardDrawCounter.increment())
                    .onErrorResume(e -> {
                                  System.err.println("Error fetching card: " + e.getMessage());
                                  return Mono.just("Error fetching card");
                                });
  }
}