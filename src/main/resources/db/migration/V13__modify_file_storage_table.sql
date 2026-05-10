ALTER TABLE file_storage
    ADD COLUMN changed_on_disk BIGINT DEFAULT 0;
