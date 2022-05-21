package config

import (
	log "github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

type Config struct {
	Port        string
	KafkaBroker string `mapstructure:"KAFKA_BROKER"`
	TopicName   string `mapstructure:"TOPIC_NAME"`
}

func LoadAndStoreConfig() Config {
	v := viper.New()              //создаем экземпляр нашего ридера для Env
	v.SetDefault("PORT", ":8080") //ставим умолчальные настройки
	v.SetDefault("KAFKA_BROKER", "localhost:29092")
	v.SetDefault("TOPIC_NAME", "tests")
	v.AutomaticEnv() //собираем наши переменные с системных

	var cfg Config

	err := v.Unmarshal(&cfg) //закидываем переменные в cfg после анмаршалинга
	if err != nil {
		log.Panic(err)
	}
	return cfg
}
