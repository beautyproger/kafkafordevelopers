package io.slurm.kafka.lecture7.actor;

import java.time.Instant;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class UserEvent {
    @NotNull
    private final String userId;
    @NotNull
    private final Instant eventTime;

    public UserEvent(@NotNull String userId, @NotNull Instant eventTime) {
        this.userId = userId;
        this.eventTime = eventTime;
    }

    public String getUserId() {
        return userId;
    }

    @NotNull
    public Instant getEventTime() {
        return eventTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEvent userEvent = (UserEvent) o;
        return userId.equals(userEvent.userId) && eventTime.equals(userEvent.eventTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventTime);
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "userId='" + userId + '\'' +
                ", eventTime=" + eventTime +
                '}';
    }
}
