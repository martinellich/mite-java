package ch.martinelli.mite4java.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateTimeEntry(
        @JsonProperty("date_at") LocalDate dateAt,
        Integer minutes,
        String note,
        @JsonProperty("user_id") Integer userId,
        @JsonProperty("project_id") Integer projectId,
        @JsonProperty("service_id") Integer serviceId,
        Boolean locked,
        Boolean force
) {
    public record Wrapper(@JsonProperty("time_entry") UpdateTimeEntry timeEntry) {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private LocalDate dateAt;
        private Integer minutes;
        private String note;
        private Integer userId;
        private Integer projectId;
        private Integer serviceId;
        private Boolean locked;
        private Boolean force;

        public Builder dateAt(LocalDate dateAt) { this.dateAt = dateAt; return this; }
        public Builder minutes(int minutes) { this.minutes = minutes; return this; }
        public Builder note(String note) { this.note = note; return this; }
        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder projectId(int projectId) { this.projectId = projectId; return this; }
        public Builder serviceId(int serviceId) { this.serviceId = serviceId; return this; }
        public Builder locked(boolean locked) { this.locked = locked; return this; }
        public Builder force(boolean force) { this.force = force; return this; }

        public UpdateTimeEntry build() {
            return new UpdateTimeEntry(dateAt, minutes, note, userId, projectId, serviceId, locked, force);
        }
    }
}
