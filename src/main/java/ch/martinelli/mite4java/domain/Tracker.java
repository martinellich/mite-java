package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Tracker(
        @JsonProperty("tracking_time_entry") TrackingEntry trackingTimeEntry,
        @JsonProperty("stopped_time_entry") TrackingEntry stoppedTimeEntry
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TrackingEntry(int id, int minutes, OffsetDateTime since) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("tracker") Tracker tracker) {}
}
