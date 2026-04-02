package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Customer(
        int id,
        String name,
        String note,
        @JsonProperty("active_hourly_rate") String activeHourlyRate,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        @JsonProperty("hourly_rates_per_service") List<HourlyRatePerService> hourlyRatesPerService,
        boolean archived,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("customer") Customer customer) {}
}
