create table input
(
    id    serial
        constraint input_pk
            primary key,
    value text,
    country text
);

alter table input
    owner to postgres;

create unique index input_id_uindex
    on input (id);



create table messages
(
    id    serial
        constraint messages_pk
            primary key,
    value text,
    status int
);

alter table messages
    owner to postgres;

create unique index messages_id_uindex
    on messages (id);