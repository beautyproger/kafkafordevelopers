package model

import "time"

type Entry struct {
	Value     string
	Country   string
	Timestamp time.Time
}
