package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import org.springframework.data.repository.CrudRepository;

public interface FileStorageRepository extends CrudRepository<FileStorage, Long> {

    /**
     * Returns {@code true} if a file storage record exists for the given host and absolute path.
     *
     * @param computeResource the compute resource (host)
     * @param absolutePath    the absolute file path on the host
     * @return {@code true} if the file has been persisted for this resource
     */
    boolean existsByComputeResourceAndAbsolutePath(ComputeResource computeResource, String absolutePath);

}
