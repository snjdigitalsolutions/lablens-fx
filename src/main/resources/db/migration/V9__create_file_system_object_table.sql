CREATE TABLE file_system_object (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    modified_time TIMESTAMP,
    permission    INT           NOT NULL,
    file_type     VARCHAR(50),
    parent_path   VARCHAR(1024) NOT NULL,
    file_name     VARCHAR(255)  NOT NULL,
    file_size     BIGINT        NOT NULL,
    track_file    BOOLEAN       NOT NULL DEFAULT FALSE
);

ALTER TABLE setting ALTER COLUMN id BIGINT AUTO_INCREMENT;

ALTER TABLE configuration_path ALTER COLUMN id BIGINT AUTO_INCREMENT;
