package processor

import (
	log "github.com/sirupsen/logrus"
	"lecture5/consumer/common"
	"time"
)

func ProcessEntry(entry *common.Entry) {
	time.Sleep(400 * time.Millisecond)

	log.Info("Value ", entry.Value)
}
