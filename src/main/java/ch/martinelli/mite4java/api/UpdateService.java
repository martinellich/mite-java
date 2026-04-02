package ch.martinelli.mite4java.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateService(
        String name,
        String note,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        Boolean billable,
        Boolean archived,
        @JsonProperty("update_hourly_rate_on_time_entries") Boolean updateHourlyRateOnTimeEntries
) {
    public record Wrapper(@JsonProperty("service") UpdateService service) {}
}
