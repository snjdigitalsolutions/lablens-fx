ALTER TABLE file_system_object ADD COLUMN compute_resource_id BIGINT;

ALTER TABLE file_system_object
    ADD CONSTRAINT fk_file_system_object_compute_resource
    FOREIGN KEY (compute_resource_id) REFERENCES compute_resource(id);

ALTER TABLE configuration_path
    ADD CONSTRAINT fk_configuration_path_compute_resource
    FOREIGN KEY (compute_resource_id) REFERENCES compute_resource(id);
