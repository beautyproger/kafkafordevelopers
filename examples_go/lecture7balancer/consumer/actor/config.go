package actor

import "time"

type Config struct {
	CountThreshold int
	WindowPeriod   time.Duration
	CleanupPeriod  time.Duration
}
