package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TimeEntryGroup {

    private int minutes;
    private double revenue;
    @JsonProperty("time_entries_params")
    private String timeEntriesParams;
    private final Map<String, Object> groupFields = new HashMap<>();

    public int getMinutes() { return minutes; }
    public double getRevenue() { return revenue; }
    public String getTimeEntriesParams() { return timeEntriesParams; }

    @JsonAnyGetter
    public Map<String, Object> getGroupFields() { return groupFields; }

    @JsonAnySetter
    public void addGroupField(String key, Object value) {
        groupFields.put(key, value);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("time_entry_group") TimeEntryGroup timeEntryGroup) {}
}
