package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import com.snjdigitalsolutions.lablensfx.orm.FileStorage;
import org.springframework.data.repository.CrudRepository;

public interface FileStorageRepository extends CrudRepository<FileStorage, Long> {

    boolean existsByComputeResourceAndAbsolutePath(ComputeResource computeResource, String absolutePath);

}
