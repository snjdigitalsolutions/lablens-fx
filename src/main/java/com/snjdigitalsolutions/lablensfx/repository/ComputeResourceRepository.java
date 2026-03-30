package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.springframework.data.repository.CrudRepository;

public interface ComputeResourceRepository extends CrudRepository<ComputeResource, Long> {

    Integer countComputeResourceBySshCommunicateIsGreaterThan(Long sshCommunicateIsGreaterThan);

}
