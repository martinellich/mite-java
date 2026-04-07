package ch.martinelli.mite4java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TimeEntry(
        int id,
        int minutes,
        @JsonProperty("date_at") LocalDate dateAt,
        String note,
        boolean billable,
        boolean locked,
        Double revenue,
        @JsonProperty("hourly_rate") Integer hourlyRate,
        @JsonProperty("user_id") Integer userId,
        @JsonProperty("user_name") String userName,
        @JsonProperty("project_id") Integer projectId,
        @JsonProperty("project_name") String projectName,
        @JsonProperty("customer_id") Integer customerId,
        @JsonProperty("customer_name") String customerName,
        @JsonProperty("service_id") Integer serviceId,
        @JsonProperty("service_name") String serviceName,
        @JsonProperty("started_time") Integer startedTime,
        Tracking tracking,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("updated_at") OffsetDateTime updatedAt
) {
    public LocalTime startTime() {
        if (startedTime == null) {
            return null;
        }
        return LocalTime.of(startedTime / 60, startedTime % 60);
    }

    public LocalTime endTime() {
        if (startedTime == null) {
            return null;
        }
        return LocalTime.of(startedTime / 60, startedTime % 60).plusMinutes(minutes);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wrapper(@JsonProperty("time_entry") TimeEntry timeEntry) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tracking(OffsetDateTime since, int minutes) {}
}
