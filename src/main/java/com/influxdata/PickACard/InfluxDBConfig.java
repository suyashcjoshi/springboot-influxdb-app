package com.influxdata.PickACard;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(InfluxProperties.class)
public class InfluxDBConfig {

  @Value("${management.metrics.export.influx.bucket}")
  private String bucket;

  @Value("${management.metrics.export.influx.org}")
  private String org;

  @Value("${management.metrics.export.influx.uri}")
  private String url;

  @Value("${management.metrics.export.influx.token}")
  private String token;

  @Value("${management.metrics.export.influx.step}")
  private Duration step;

  // Need to implement this interface
  @Bean
  public InfluxConfig influxConfig() {
    return new InfluxConfig() {
      @Override
      public String bucket() {
        return bucket;
      }

      @Override
      public String org() {
        return org;
      }

      @Override
      public String uri() {
        return url;
      }

      @Override
      public String token() {
        return token;
      }

      @Override
      public Duration step() {
        return step != null ? step : Duration.ofSeconds(10);
      }

      @Override
      public String get(String key) {
        return null;
      }
    };
  }

  @Bean
  public MeterRegistry meterRegistry(InfluxConfig influxConfig) {
    return new InfluxMeterRegistry(influxConfig, Clock.SYSTEM);
  }
}