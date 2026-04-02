package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Bookmark(
        int id,
        String name,
        String query,
        String type,
        @JsonProperty("account_id") int accountId,
        @JsonProperty("user_id") Integer userId,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("time_entry_bookmark") Bookmark bookmark) {}
}
