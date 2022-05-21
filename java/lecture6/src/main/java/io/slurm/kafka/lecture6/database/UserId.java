package io.slurm.kafka.lecture6.database;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class UserId {
    @NotNull
    private final String value;

    public UserId(@NotNull String value) {
        this.value = value;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "value='" + value + '\'' +
                '}';
    }
}
