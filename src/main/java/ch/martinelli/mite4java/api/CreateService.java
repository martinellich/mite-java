package ch.martinelli.mite4java.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateService(
        String name,
        String note,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        Boolean billable,
        Boolean archived
) {
    public record Wrapper(@JsonProperty("service") CreateService service) {}

    public CreateService(String name) {
        this(name, null, null, null, null);
    }
}
