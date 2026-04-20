package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "file_system_object")
@Getter
@Setter
public class FileSystemObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "modified_time")
    private Instant modifiedTime;
    @Column(name = "permission")
    private int permission;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "parent_path")
    private String parentPath;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_size")
    private long fileSize;
    @Column(name = "track_file")
    private boolean trackFile;
    @ManyToOne
    @JoinColumn(name = "compute_resource_id")
    private ComputeResource computeResource;
}
