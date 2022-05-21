package repo

import (
	"context"
	"github.com/jackc/pgx/v4"
)

type OffsetsRepo struct {
	conn *pgx.Conn
}

func NewOffsetsRepo(config Config) *OffsetsRepo {
	repo := &OffsetsRepo{}
	url := config.GetDBConnection()
	var err error
	repo.conn, err = pgx.Connect(context.Background(), url)

	if err != nil {
		panic(err)
	}

	return repo
}

//TODO do transactions
func (repo *OffsetsRepo) GetOffsetForTopic(ctx context.Context, topic string) (uint64, error) {
	query := "SELECT offset FROM offset WHERE topic_name = $1"

	rows, err := repo.conn.Query(ctx, query, topic)
	if err != nil {
		return 0, err
	}

	for rows.Next() {
		var offset uint64
		err := rows.Scan(&offset)
		if err != nil {
			return 0, err
		}
		return offset, nil
	}

	return 0, nil
}

//TODO do transactions

func (repo *OffsetsRepo) SetOffsetForTopic(ctx context.Context, topic string, offset uint64) error {
	query := "INSERT INTO offsets(topic, offset) VALUES ($1, $2) ON CONFLICT (topic) DO UPDATE SET offset=$2"

	_, err := repo.conn.Exec(ctx, query, topic, offset)
	if err != nil {
		return err
	}

	return nil
}

func (repo *OffsetsRepo) CloseConnection(ctx context.Context) {
	err := repo.conn.Close(ctx)
	if err != nil {
		panic(err)
	}
}
