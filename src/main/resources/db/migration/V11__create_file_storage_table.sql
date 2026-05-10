CREATE TABLE file_storage (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    compute_resource_id     BIGINT          NOT NULL,
    file_name               VARCHAR(255)    NOT NULL,
    file_md5                VARCHAR(32)     NOT NULL,
    file_size               BIGINT          NOT NULL,
    created_time            TIMESTAMP       NOT NULL ,
    file_date               BLOB
);

ALTER TABLE file_storage
    ADD CONSTRAINT fk_file_storage_compute_resource
    FOREIGN KEY (compute_resource_id) REFERENCES compute_resource(id);
