package io.slurm.kafka.lecture7.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLimitReach {
    private static final Logger log = LoggerFactory.getLogger(UserLimitReach.class);

    @NotNull
    @JsonProperty("user_id")
    private final String userId;
    @NotNull
    @JsonProperty("reach_time")
    private final Instant reachTime;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserLimitReach(
            @JsonProperty("user_id") @NotNull String userId,
            @JsonProperty("reach_time") @NotNull Instant reachTime
    ) {
        this.userId = userId;
        this.reachTime = reachTime;
    }

    public @NotNull String getUserId() {
        return userId;
    }

    public @NotNull Instant getReachTime() {
        return reachTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserLimitReach that = (UserLimitReach) o;
        return userId.equals(that.userId) && reachTime.equals(that.reachTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, reachTime);
    }

    @Override
    public String toString() {
        return "UserLimitReach{" +
                "userId='" + userId + '\'' +
                ", reachTime=" + reachTime +
                '}';
    }
}
