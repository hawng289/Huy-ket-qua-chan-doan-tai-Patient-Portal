CREATE TABLE hl7_message
(
    id                serial primary key,
    hl7               text                     not null,
    message_type      varchar(64)              NOT NULL,
    message_id        varchar(64)              NOT NULL,
    message_time      timestamp with time zone not null,

    created_time      timestamp with time zone not null,
    creator_id        integer                  not null default 1,
    last_updated_time timestamp with time zone,
    last_updated_id   integer
);
CREATE INDEX idx_hl7_message_time on hl7_message (message_time);
CREATE INDEX idx_hl7_message_type on hl7_message (message_type);

CREATE TABLE wl_message
(
    id                serial primary key,
    hl7_message_id    integer                  NOT NULL references hl7_message (id),
    accession_number  varchar(64),
    order_number      varchar(64)              not null,

    request_url       text,
    request_time      varchar(255),
    request_count     smallint,

    succeeded         boolean                  not null default false,
    errors            text,

    created_time      timestamp with time zone not null,
    creator_id        integer                  not null default 1,
    last_updated_time timestamp with time zone,
    last_updated_id   integer
);
CREATE TABLE ris_message
(
    id                serial primary key,
    hl7_message_id    integer                  NOT NULL references hl7_message (id),
    pid               varchar(64),
    accession_number  varchar(64),
    order_number      varchar(64)              not null,

    request_url       text,
    request_time      varchar(255),
    request_count     smallint,

    succeeded         boolean                  not null default false,
    errors            text,

    created_time      timestamp with time zone not null,
    creator_id        integer                  not null default 1,
    last_updated_time timestamp with time zone,
    last_updated_id   integer
);
create index idx_ris_message_order_number on ris_message(order_number);
-- create index idx_pid on ris_message(pid);

-- Xoa cac truong khac
-- Them moi truong ris_report_id
CREATE TABLE report
(
    id                 serial primary key,
    ris_report_id      integer,
    accession_number   varchar(50) not null,
    order_number       varchar(50),
    patient            text,
    study_IUID         varchar(255),
    order_datetime     varchar(50),
    modality_room      varchar(50),
    request_number     varchar(50),
    procedure_code     varchar(50),
    procedure_name     text,
    modality           varchar(50),
    modality_code      varchar(50),
    creator            text,
    approver           text,
    operators          text,
    body_html          text,
    conclusion_html    text,
    note               text,
    created_datetime   varchar(50),
    approved_datetime  varchar(50),
    operation_datetime varchar(50),
    consumables        text,
    key_images         text,
    -- Them moi truong is_create
    is_create          boolean     not null,
    his_status         boolean,
    re_call            boolean                  default false,
    num_of_retries     smallint                 default 0,
--     Thoi gian luu report vao db
    msg_time           timestamp with time zone default now()
);
create index idx_report_accession_number on report (accession_number);

-- Them moi bang luu file
create table file_signed
(
    id                serial primary key,
    accession_number  varchar(50)              not null,
    request_number    varchar(50),
    procedure_code    varchar(50),
    signer            text,
    pdf_path          text,
    re_call           boolean                           default false,
    temp_path         text,
    created_time      timestamp with time zone not null,
    creator_id        integer                  not null default 1,
    last_updated_time timestamp with time zone,
    last_updated_id   integer
);
create index idx_file_signed_accession_number on file_signed (accession_number);

-- Them moi bang da gui
create table report_sent
(
    id                serial primary key,
    accession_number  varchar(50)              not null,
    report_id         integer                  not null references report (id),
    file_id           integer references file_signed (id),
    his_status        boolean,
    error_detail      text,
    -- Them moi truong request
    request           text,

    created_time      timestamp with time zone not null,
    creator_id        integer                  not null default 1,
    last_updated_time timestamp with time zone,
    last_updated_id   integer
);

-- Them moi bang lock_order
create table lock_order(
    id serial primary key,
    order_number varchar(64),
    pid varchar(64),
    accession_number varchar(64),
    request text,
    response text,

    created_time      timestamp with time zone not null,
    creator_id        integer                  not null default 1,
    last_updated_time timestamp with time zone,
    last_updated_id   integer
);

-- -- them moi bang patient_portal_report
create table patient_portal_report
(
    id               serial primary key,
    accession_number varchar(50) not null,
    procedure_code   varchar(50) not null

);

-- Them moi bang push_notification
create table push_notification
(
    id               serial primary key,
    accession_number varchar not null,
    pid              varchar(20),
    status           boolean
);
