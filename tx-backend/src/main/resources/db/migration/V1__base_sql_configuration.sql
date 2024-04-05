-- DROP SEQUENCE public.alert_id_seq;

CREATE SEQUENCE public.alert_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.investigation_id_seq;

CREATE SEQUENCE public.investigation_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.shell_descriptor_id_seq;

CREATE SEQUENCE public.shell_descriptor_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- public.alert definition

-- Drop table

-- DROP TABLE public.alert;

CREATE TABLE public.alert
(
    id int8 NOT NULL,
    bpn            varchar(255)  NULL,
    close_reason   varchar(1000) NULL,
    created        timestamp     NULL,
    description    varchar(1000) NULL,
    status         varchar(50)   NULL,
    side           varchar(50)   NULL,
    accept_reason  varchar(1000) NULL,
    decline_reason varchar(1000) NULL,
    updated        timestamp     NULL,
    error_message  varchar(255)  NULL,
    CONSTRAINT alert_pkey PRIMARY KEY (id)
);


-- public.asset_as_built_alert_notifications definition

-- Drop table

-- DROP TABLE public.asset_as_built_alert_notifications;

CREATE TABLE public.asset_as_built_alert_notifications
(
    alert_notification_id varchar(255) NOT NULL,
    asset_id              varchar(255) NOT NULL
);


-- public.asset_as_planned_alert_notifications definition

-- Drop table

-- DROP TABLE public.asset_as_planned_alert_notifications;

CREATE TABLE public.asset_as_planned_alert_notifications
(
    alert_notification_id varchar(255) NOT NULL,
    asset_id              varchar(255) NOT NULL
);


-- public.assets_as_built definition

-- Drop table

-- DROP TABLE public.assets_as_built;

CREATE TABLE public.assets_as_built
(
    id                    varchar(255) NOT NULL,
    customer_part_id      varchar(255) NULL,
    id_short              varchar(255) NULL,
    manufacturer_id       varchar(255) NULL,
    manufacturer_name     varchar(255) NULL,
    manufacturer_part_id  varchar(255) NULL,
    manufacturing_country varchar(255) NULL,
    manufacturing_date    timestamp    NULL,
    name_at_customer      varchar(255) NULL,
    name_at_manufacturer  varchar(255) NULL,
    quality_type          varchar(50)  NULL,
    van                   varchar(255) NULL,
    "owner"               varchar(50)  NULL,
    in_investigation      bool         NOT NULL DEFAULT false,
    active_alert          bool         NOT NULL DEFAULT false,
    semantic_model_id     varchar(255) NULL,
    semantic_data_model   varchar(50)  NULL,
    classification        varchar(255) NULL,
    product_type          varchar(255) NULL,
    traction_battery_code varchar(255) NULL,
    CONSTRAINT asset_pkey PRIMARY KEY (id)
);


-- public.assets_as_planned definition

-- Drop table

-- DROP TABLE public.assets_as_planned;

CREATE TABLE public.assets_as_planned
(
    id                   varchar(255) NOT NULL,
    id_short             varchar(255) NULL,
    manufacturer_part_id varchar(255) NULL,
    name_at_manufacturer varchar(255) NULL,
    quality_type         varchar(50)  NULL,
    classification       varchar(255) NULL,
    "owner"              varchar(50)  NULL,
    semantic_data_model  varchar(50)  NULL,
    in_investigation     bool         NOT NULL DEFAULT false,
    active_alert         bool         NOT NULL DEFAULT false,
    validity_period_from varchar(255) NULL,
    validity_period_to   varchar(255) NULL,
    function_valid_until varchar(255) NULL,
    function_valid_from  varchar(255) NULL,
    "function"           varchar(255) NULL,
    manufacturer_name    varchar(255) NULL,
    van                  varchar(255) NULL,
    semantic_model_id    varchar(255) NULL,
    catenax_site_id      varchar(255) NULL,
    CONSTRAINT assets_as_planned_pkey PRIMARY KEY (id)
);


-- public.assets_as_planned_alerts definition

-- Drop table

-- DROP TABLE public.assets_as_planned_alerts;

CREATE TABLE public.assets_as_planned_alerts
(
    alert_id int8         NOT NULL,
    asset_id varchar(255) NOT NULL
);


-- public.assets_as_planned_childs definition

-- Drop table

-- DROP TABLE public.assets_as_planned_childs;

CREATE TABLE public.assets_as_planned_childs
(
    asset_as_planned_id varchar(255) NOT NULL,
    id                  varchar(255) NULL,
    id_short            varchar(255) NULL
);


-- public.assets_as_planned_investigations definition

-- Drop table

-- DROP TABLE public.assets_as_planned_investigations;

CREATE TABLE public.assets_as_planned_investigations
(
    investigation_id int8         NOT NULL,
    asset_id         varchar(255) NOT NULL
);


-- public.assets_as_planned_notifications definition

-- Drop table

-- DROP TABLE public.assets_as_planned_notifications;

CREATE TABLE public.assets_as_planned_notifications
(
    notification_id varchar(255) NOT NULL,
    asset_id        varchar(255) NOT NULL
);


-- public.bpn_storage definition

-- Drop table

-- DROP TABLE public.bpn_storage;

CREATE TABLE public.bpn_storage
(
    manufacturer_id   varchar(255) NOT NULL,
    manufacturer_name varchar(255) NULL,
    url               varchar(255) NULL,
    created           timestamptz  NULL,
    updated           timestamptz  NULL,
    CONSTRAINT bpn_storage_pkey PRIMARY KEY (manufacturer_id),
    CONSTRAINT bpn_storage_url_key UNIQUE (url)
);


-- public.investigation definition

-- Drop table

-- DROP TABLE public.investigation;

CREATE TABLE public.investigation
(
    id int8 NOT NULL,
    bpn            varchar(255)  NULL,
    close_reason   varchar(1000) NULL,
    created        timestamp     NULL,
    description    varchar(1000) NULL,
    status         varchar(50)   NULL,
    updated        timestamp     NULL,
    side           varchar(50)   NULL,
    accept_reason  varchar(1000) NULL,
    decline_reason varchar(1000) NULL,
    error_message  varchar(255)  NULL,
    CONSTRAINT investigation_pkey PRIMARY KEY (id)
);


-- public.shedlock definition

-- Drop table

-- DROP TABLE public.shedlock;

CREATE TABLE public.shedlock
(
    "name"     varchar(64)  NOT NULL,
    lock_until timestamp    NOT NULL,
    locked_at  timestamp    NOT NULL,
    locked_by  varchar(255) NOT NULL,
    CONSTRAINT shedlock_pkey PRIMARY KEY (name)
);


-- public.shell_descriptor definition

-- Drop table

-- DROP TABLE public.shell_descriptor;

CREATE TABLE public.shell_descriptor
(
    id              serial4     NOT NULL,
    created         timestamptz NOT NULL,
    updated         timestamptz NOT NULL,
    global_asset_id text        NOT NULL,
    CONSTRAINT shell_descriptor_global_asset_id_key UNIQUE (global_asset_id),
    CONSTRAINT shell_descriptor_pkey PRIMARY KEY (id)
);


-- public.submodel definition

-- Drop table

-- DROP TABLE public.submodel;

CREATE TABLE public.submodel
(
    id       varchar(255) NOT NULL,
    submodel varchar      NULL,
    CONSTRAINT submodel_pkey PRIMARY KEY (id)
);


-- public.traction_battery_code_subcomponent definition

-- Drop table

-- DROP TABLE public.traction_battery_code_subcomponent;

CREATE TABLE public.traction_battery_code_subcomponent
(
    traction_battery_code              varchar(255) NOT NULL,
    subcomponent_traction_battery_code varchar(255) NOT NULL,
    product_type                       varchar(255) NULL
);


-- public.alert_notification definition

-- Drop table

-- DROP TABLE public.alert_notification;

CREATE TABLE public.alert_notification
(
    id                        varchar(255) NOT NULL,
    contract_agreement_id     varchar(255) NULL,
    edc_url                   varchar(255) NULL,
    notification_reference_id varchar(255) NULL,
    send_to                   varchar(255) NULL,
    created_by                varchar(255) NULL,
    alert_id                  int8         NULL,
    target_date               timestamp    NULL,
    severity                  int4         NULL,
    created_by_name           varchar(255) NULL,
    send_to_name              varchar(255) NULL,
    edc_notification_id       varchar(255) NULL,
    status                    varchar(255) NULL,
    created                   timestamptz  NULL,
    updated                   timestamptz  NULL,
    message_id                varchar(255) NULL,
    is_initial                bool         NULL,
    CONSTRAINT alert_notification_pkey PRIMARY KEY (id),
    CONSTRAINT fk_alert FOREIGN KEY (alert_id) REFERENCES public.alert (id)
);


-- public.assets_as_built_alerts definition

-- Drop table

-- DROP TABLE public.assets_as_built_alerts;

CREATE TABLE public.assets_as_built_alerts
(
    alert_id int8         NOT NULL,
    asset_id varchar(255) NOT NULL,
    CONSTRAINT fk_alert FOREIGN KEY (alert_id) REFERENCES public.alert (id),
    CONSTRAINT fk_asset_entity FOREIGN KEY (asset_id) REFERENCES public.assets_as_built (id)
);


-- public.assets_as_built_childs definition

-- Drop table

-- DROP TABLE public.assets_as_built_childs;

CREATE TABLE public.assets_as_built_childs
(
    asset_as_built_id varchar(255) NOT NULL,
    id                varchar(255) NULL,
    id_short          varchar(255) NULL,
    CONSTRAINT fk_asset FOREIGN KEY (asset_as_built_id) REFERENCES public.assets_as_built (id)
);


-- public.assets_as_built_investigations definition

-- Drop table

-- DROP TABLE public.assets_as_built_investigations;

CREATE TABLE public.assets_as_built_investigations
(
    investigation_id int8         NOT NULL,
    asset_id         varchar(255) NOT NULL,
    CONSTRAINT fk_asset_entity FOREIGN KEY (asset_id) REFERENCES public.assets_as_built (id),
    CONSTRAINT fk_investigation FOREIGN KEY (investigation_id) REFERENCES public.investigation (id)
);


-- public.assets_as_built_parents definition

-- Drop table

-- DROP TABLE public.assets_as_built_parents;

CREATE TABLE public.assets_as_built_parents
(
    asset_as_built_id varchar(255) NOT NULL,
    id                varchar(255) NULL,
    id_short          varchar(255) NULL,
    CONSTRAINT fk_asset FOREIGN KEY (asset_as_built_id) REFERENCES public.assets_as_built (id)
);


-- public.investigation_notification definition

-- Drop table

-- DROP TABLE public.investigation_notification;

CREATE TABLE public.investigation_notification
(
    id                        varchar(255) NOT NULL,
    contract_agreement_id     varchar(255) NULL,
    edc_url                   varchar(255) NULL,
    notification_reference_id varchar(255) NULL,
    send_to                   varchar(255) NULL,
    created_by                varchar(255) NULL,
    investigation_id          int8         NULL,
    target_date               timestamp    NULL,
    severity                  int4         NULL,
    created_by_name           varchar(255) NULL,
    send_to_name              varchar(255) NULL,
    edc_notification_id       varchar(255) NULL,
    status                    varchar(255) NULL,
    created                   timestamptz  NULL,
    updated                   timestamptz  NULL,
    message_id                varchar(255) NULL,
    is_initial                bool         NULL,
    CONSTRAINT notification_pkey PRIMARY KEY (id),
    CONSTRAINT fk_investigation FOREIGN KEY (investigation_id) REFERENCES public.investigation (id)
);


-- public.assets_as_built_notifications definition

-- Drop table

-- DROP TABLE public.assets_as_built_notifications;

CREATE TABLE public.assets_as_built_notifications
(
    notification_id varchar(255) NOT NULL,
    asset_id        varchar(255) NOT NULL,
    CONSTRAINT fk_notification FOREIGN KEY (notification_id) REFERENCES public.investigation_notification (id)
);
