package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HourlyRatePerService(
        @JsonProperty("service_id") int serviceId,
        @JsonProperty("hourly_rate") int hourlyRate
) {}
