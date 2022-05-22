package actor

import (
	"serialisation/consumer/model"
	"time"
)

type PartitionActor struct {
	config      *Config
	subActorMap map[string]*EntryActor
	nextCleanup time.Time
}

func NewPartitionActor(config *Config) *PartitionActor {
	actor := &PartitionActor{}
	actor.subActorMap = make(map[string]*EntryActor)
	actor.config = config
	actor.nextCleanup = time.Time{}
	return actor
}

func (actor *PartitionActor) AddEvent(entry *model.Entry) int {
	if actor.nextCleanup.Before(entry.Timestamp) {
		actor.CleanUp(actor.nextCleanup)
	}

	var userActor *EntryActor
	var ok bool

	if userActor, ok = actor.subActorMap[entry.Country]; !ok {
		actor.subActorMap[entry.Country] = NewEntryActor(actor.config)
		userActor = actor.subActorMap[entry.Country]
	}
	return userActor.AddEvent(entry)
}

func (actor *PartitionActor) CleanUp(execTime time.Time) {
	lowThreshold := execTime.Add(-actor.config.WindowPeriod)
	for k, v := range actor.subActorMap {
		if v.CleanUp(lowThreshold) == CLEAN {
			delete(actor.subActorMap, k)
		}
	}
	actor.nextCleanup = execTime.Add(actor.config.CleanupPeriod)
}

/*


   public Map<String, Long> queryState() {
       return subActorMap.entrySet().stream()
               .collect(Collectors.toMap(
                       entry -> entry.getKey(),
                       entry -> entry.getValue().queryState()
               ));
   }

*/
