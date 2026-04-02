package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Service(
        int id,
        String name,
        String note,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        boolean billable,
        boolean archived,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("service") Service service) {}
}
