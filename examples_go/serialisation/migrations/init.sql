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

