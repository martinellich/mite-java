package ch.martinelli.mite4java.api;

import ch.martinelli.mite4java.domain.HourlyRatePerService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateProject(
        String name,
        String note,
        @JsonProperty("customer_id") Integer customerId,
        Integer budget,
        @JsonProperty("budget_type") String budgetType,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        @JsonProperty("active_hourly_rate") String activeHourlyRate,
        @JsonProperty("hourly_rates_per_service") List<HourlyRatePerService> hourlyRatesPerService,
        Boolean archived
) {
    public record Wrapper(@JsonProperty("project") CreateProject project) {}

    public CreateProject(String name) {
        this(name, null, null, null, null, null, null, null, null);
    }
}
