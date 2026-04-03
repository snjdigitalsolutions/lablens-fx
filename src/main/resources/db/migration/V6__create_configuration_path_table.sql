CREATE TABLE configuration_path (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compute_resource_id INT,
    configuration_path VARCHAR(256),
    requires_elevation INT
);
