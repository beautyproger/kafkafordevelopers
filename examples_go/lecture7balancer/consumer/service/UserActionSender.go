package service

import log "github.com/sirupsen/logrus"
import (
	"serialisation/consumer/model"
)

type EntrySender struct {
}

func NewEntrySender(brokers []string, topic string) *EntrySender {
	return &EntrySender{}
	//TODO init
}

func (sender *EntrySender) SendUserAction(reached model.LimitReached) error {
	log.Info("Reached action ", reached)
	//TODO отправлять сообщения
	return nil
}
