create table input
(
    id    serial
        constraint input_pk
            primary key,
    value text,
    country text
);

create unique index input_id_uindex
    on input (id);



create table offsets
(
    topic_name text not null
        constraint offsets_pk
            primary key,
    "offset"   bigint
);

create unique index offsets_topic_name_uindex
    on offsets (topic_name);

