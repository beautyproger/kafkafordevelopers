package main

import (
	"context"
	"fmt"
	"github.com/jackc/pgx/v4"
)

type RepoConfig struct {
	host     string
	port     string
	user     string
	password string
	dbname   string
}

type WorkerRepo struct {
	conn *pgx.Conn
}

type Message struct {
	id    int
	value string
}

func NewWorkerRepo(config *RepoConfig) *WorkerRepo {
	repo := &WorkerRepo{}
	url := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", config.user, config.password, config.host, config.port, config.dbname)
	var err error
	repo.conn, err = pgx.Connect(context.Background(), url)

	if err != nil {
		panic(err)
	}

	return repo
}

func (repo *WorkerRepo) AddMessage(ctx context.Context, value string) error {
	query := "INSERT INTO messages (value, status) VALUES ($1, 0)"

	_, err := repo.conn.Exec(ctx, query, value)
	return err
}

func (repo *WorkerRepo) BeginTransaction(ctx context.Context) (transaction pgx.Tx, err error) {
	return repo.conn.BeginTx(ctx, pgx.TxOptions{})
}

func (repo *WorkerRepo) MarkedMessageAsSent(ctx context.Context, message Message, transaction pgx.Tx) error {
	query := "UPDATE messages SET status = 1 WHERE id = $1"

	_, err := transaction.Exec(ctx, query, message.id)

	return err
}

func (repo *WorkerRepo) GetMessagesToSend(ctx context.Context, limit int, transaction pgx.Tx) ([]Message, error) {
	var messages []Message

	query := "SELECT id, value FROM messages WHERE status = 0 LIMIT $1"
	rows, err := transaction.Query(ctx, query, limit)
	if err != nil {
		return []Message{}, err
	}

	for rows.Next() {
		value := Message{}
		err := rows.Scan(&value)
		if err != nil {
			return []Message{}, err
		}
		messages = append(messages, value)
	}

	return messages, nil

}
