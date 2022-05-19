package io.slurm.kafka.utils.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class UserAction {
    @NotNull
    @JsonProperty("message_id")
    private final String messageId;
    @NotNull
    @JsonProperty("user_id")
    private final String userId;
    @JsonProperty("action_type")
    private final long actionType;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserAction(
            @JsonProperty("message_id") String messageId,
            @JsonProperty("user_id") @NotNull String userId,
            @JsonProperty("action_type") long actionType
    ) {
        this.messageId = Objects.requireNonNull(messageId);
        this.userId =  Objects.requireNonNull(userId);
        this.actionType = actionType;
    }

    @NotNull
    public String getMessageId() {
        return messageId;
    }

    @NotNull
    public String getUserId() {
        return userId;
    }


    public long getActionType() {
        return actionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAction that = (UserAction) o;
        return actionType == that.actionType && messageId.equals(that.messageId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, userId, actionType);
    }

    @Override
    public String toString() {
        return "UserAction{" +
                "messageId=" + messageId +
                ", userId='" + userId + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
