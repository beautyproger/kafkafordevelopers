package actor

import (
	"container/list"
	"serialisation/consumer/model"
	"time"
)

type EntryActor struct {
	config         *Config
	activationTime time.Time
	entries        *list.List
}

func NewEntryActor(config *Config) *EntryActor {
	actor := &EntryActor{
		config: config,
	}
	actor.entries = list.New()
	return actor
}

func (receiver *EntryActor) AddEvent(entry *model.Entry) int {
	receiver.entries.PushFront(entry)

	//TODO если мы превысили CountThreshold то делаем клинап по времени -WindowPeriod чтобы скинуть все что старше интересующего нас интервала.
	//TODO если посте клинапа у нас все еще больше чем CountThreshold и activationTime.IsZero() то возвращаем ACTIVATION, ставим activationTime в eventTime

	return NONE
}

func (receiver *EntryActor) CleanUp(execTime time.Time) int {
	//TODO очищаем старые записи
	//TODO если активейшн тайм раньше execTime то сбрасываем в пустой Time
	//TODO если лист пуст и активейшн тайм пуст то возвращаем CLEAN, а если нет то DIRTY

	return CLEAN
}

/*

    public long queryState() {
        return events.size();
    }

    public enum CleanUpResult{
        CLEAN,
        DIRTY,
    }
}
*/
