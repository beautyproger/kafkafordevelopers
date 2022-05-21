package io.slurm.kafka.lecture6.database;

import java.util.Objects;

public class ActionTypeId {
    private final long value;

    public ActionTypeId(long value) {
        this.value = value;
    }

    public long getValue() {
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
        ActionTypeId that = (ActionTypeId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ActionType{" +
                "value=" + value +
                '}';
    }
}
