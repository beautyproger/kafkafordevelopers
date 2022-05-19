package model

type UserAction struct {
	MessageId  string `json:"message_id"`
	UserId     string `json:"user_id"`
	ActionType int    `json:"action_type"`
}
