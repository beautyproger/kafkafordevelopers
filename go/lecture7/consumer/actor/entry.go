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

	if receiver.entries.Len() > receiver.config.CountThreshold {
		eventTime := entry.Timestamp
		receiver.CleanUp(eventTime.Add(-receiver.config.WindowPeriod))
		if receiver.activationTime.IsZero() && receiver.entries.Len() > receiver.config.CountThreshold {
			receiver.activationTime = eventTime
			return ACTIVATION
		}
	}
	return NONE
}

func (receiver *EntryActor) CleanUp(execTime time.Time) int {
	for e := receiver.entries.Front(); e != nil; {
		if e.Value.(*model.Entry).Timestamp.Before(execTime) {
			tmp := e.Next()
			receiver.entries.Remove(e)
			e = tmp
		} else {
			e = e.Next()
		}

	}
	if receiver.activationTime.Before(execTime) {
		receiver.activationTime = time.Time{}
	}
	if receiver.entries.Len() == 0 && receiver.activationTime.IsZero() {
		return CLEAN
	} else {
		return DIRTY
	}

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
