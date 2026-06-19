package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileStorageRepository extends CrudRepository<FileStorage, Long> {

    /**
     * Returns {@code true} if a file storage record exists for the given host and absolute path.
     *
     * @param computeResource the compute resource (host)
     * @param absolutePath    the absolute file path on the host
     * @return {@code true} if the file has been persisted for this resource
     */
    boolean existsByComputeResourceAndAbsolutePath(ComputeResource computeResource,
                                                   String absolutePath
    );

    /**
     * Returns the count of file storage records that are marked as changed
     * but not as resolved
     *
     * @return the count of file storage records
     */
    @Query("SELECT count(*) FROM FileStorage fs WHERE fs.changedOnDisk = true AND fs.resolved = false")
    Integer countOfChangedAndUnresolved();

    /**
     * Get a list of {@link FileStorage} based on changed and resolved.
     * @param changedOnDisk true if the configuration has been changed
     * @param resolved true when the configuration change has been resolved / accepted
     * @return list of {@link FileStorage} matching conditions.
     */
    List<FileStorage> findAllByChangedOnDiskAndResolved(Boolean changedOnDisk,
                                                        Boolean resolved
    );

}
