package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        int id,
        String name,
        String email,
        String note,
        boolean archived,
        String role,
        String language,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("user") User user) {}
}
