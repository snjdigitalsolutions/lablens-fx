package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.ComputeResource;
import org.springframework.data.repository.CrudRepository;

public interface ComputeResourceRepository extends CrudRepository<ComputeResource, Long> {

    /**
     * Counts compute resources whose last SSH communication timestamp is after the given value.
     * Used to determine how many hosts have been recently reachable.
     *
     * @param sshCommunicateIsGreaterThan the minimum timestamp (exclusive) in epoch milliseconds
     * @return count of resources with a communication time greater than the threshold
     */
    Integer countComputeResourceBySshCommunicateIsGreaterThan(Long sshCommunicateIsGreaterThan);

}
