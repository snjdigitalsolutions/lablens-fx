ALTER TABLE file_storage
    ADD COLUMN resolved BIGINT DEFAULT 0;

ALTER TABLE file_storage
    ADD COLUMN parent BIGINT DEFAULT 0;

ALTER TABLE file_storage
    ADD COLUMN child BIGINT DEFAULT 0;
