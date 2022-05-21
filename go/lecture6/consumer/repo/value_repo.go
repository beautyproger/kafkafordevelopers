package repo

import (
	"context"
	"fmt"
	"github.com/jackc/pgx/v4"
)

type ValueRepo struct {
	conn *pgx.Conn
}

func NewValueRepo(config Config) *ValueRepo {
	repo := &ValueRepo{}
	url := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", config.user, config.password, config.host, config.port, config.dbname)
	var err error
	repo.conn, err = pgx.Connect(context.Background(), url)

	if err != nil {
		panic(err)
	}

	return repo
}

func (repo *ValueRepo) CloseConnection(ctx context.Context) {
	err := repo.conn.Close(ctx)
	if err != nil {
		panic(err)
	}
}

func (repo *ValueRepo) AddValue(ctx context.Context, value string) error {
	query := "INSERT INTO input (value) VALUES ($1)"

	_, err := repo.conn.Exec(ctx, query, value)

	return err
}

func (repo *ValueRepo) ReadValues(ctx context.Context) ([]string, error) {
	var vals []string

	query := "SELECT value FROM input"
	rows, err := repo.conn.Query(ctx, query)
	if err != nil {
		return []string{}, err
	}

	for rows.Next() {
		var value string
		err := rows.Scan(&value)
		if err != nil {
			return []string{}, err
		}
		vals = append(vals, value)
	}

	return vals, nil
}
