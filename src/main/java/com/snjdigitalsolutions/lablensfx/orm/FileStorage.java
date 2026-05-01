package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "file_storage")
@Getter
@Setter
public class FileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "compute_resource_id")
    private ComputeResource computeResource;
    @Column(name = "absolute_file_path")
    private String absolutePath;
    @Column(name = "file_md5")
    private String fileMd5;
    @Column(name = "file_size")
    private long fileSize;
    @Column(name = "created_time")
    private Instant createdTime;
    @Column(name = "file_date")
    private byte[] fileDate;
}
