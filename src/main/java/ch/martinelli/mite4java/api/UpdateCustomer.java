package ch.martinelli.mite4java.api;

import ch.martinelli.mite4java.domain.HourlyRatePerService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateCustomer(
        String name,
        String note,
        @JsonProperty("active_hourly_rate") String activeHourlyRate,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        @JsonProperty("hourly_rates_per_service") List<HourlyRatePerService> hourlyRatesPerService,
        Boolean archived,
        @JsonProperty("update_hourly_rate_on_time_entries") Boolean updateHourlyRateOnTimeEntries
) {
    public record Wrapper(@JsonProperty("customer") UpdateCustomer customer) {}
}
