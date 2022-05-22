package repo

import "fmt"

type Config struct {
	host     string
	port     string
	user     string
	password string
	dbname   string
}

func (config *Config) GetDBConnection() string {
	return fmt.Sprintf("postgres://%s:%s@%s:%s/%s", config.user, config.password, config.host, config.port, config.dbname)
}
