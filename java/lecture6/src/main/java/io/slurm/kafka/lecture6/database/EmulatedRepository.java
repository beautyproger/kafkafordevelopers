package io.slurm.kafka.lecture6.database;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmulatedRepository {
    private static final Logger log = LoggerFactory.getLogger(EmulatedRepository.class);

    private final Object commitLock = new Object();
    private volatile DatabaseState globalState = new DatabaseState();

    private final ThreadLocal<DatabaseState> uncommittedState = new ThreadLocal<>();

    public long getOffsetForPartition(TopicPartition partition) {
        DatabaseState copy = this.globalState;

        Long offset = this.globalState.offsetsTable.get(partition);
        return offset == null? 0: offset;
    }

    public void addActionInvocation(ActionTypeId action) {
        uncommittedState.get().actionInvocationTable.merge(action, 1L, (val1, val2) -> val1 + val2);
    }

    public void writeOffset(TopicPartition partition, long offset) {
        uncommittedState.get().offsetsTable.put(partition, offset);
    }


    public void startTransaction() {
        uncommittedState.set(new DatabaseState());
    }

    public void rollback() {
        uncommittedState.set(null);
    }

    public void commit() {
        DatabaseState newGlobalState = new DatabaseState();
        DatabaseState uncommitted = uncommittedState.get();

        synchronized (commitLock) {
            newGlobalState.offsetsTable = new HashMap<>(globalState.offsetsTable);
            newGlobalState.actionInvocationTable = new HashMap<>(globalState.actionInvocationTable);
            for (Map.Entry<ActionTypeId, Long> entry : uncommitted.actionInvocationTable.entrySet()) {
                newGlobalState.actionInvocationTable.merge(
                        entry.getKey(),
                        entry.getValue(),
                        (val1, val2) -> val1 + val2
                );
            }
            newGlobalState.offsetsTable.putAll(uncommitted.offsetsTable);
            globalState = newGlobalState;
        }
        uncommittedState.set(new DatabaseState());

    }

    public DatabaseState queryState() {
        return globalState;
    }


    public static class DatabaseState{
        public HashMap<ActionTypeId, Long> actionInvocationTable = new HashMap<>();
        public HashMap<TopicPartition, Long> offsetsTable = new HashMap<>();
    }
}
