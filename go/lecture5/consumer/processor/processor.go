package processor

import (
	log "github.com/sirupsen/logrus"
	"lecture5/consumer/model"
	"time"
)

func ProcessEntry(action *model.UserAction) {
	time.Sleep(400 * time.Millisecond)

	log.Info("Value ", action.MessageId)

}
