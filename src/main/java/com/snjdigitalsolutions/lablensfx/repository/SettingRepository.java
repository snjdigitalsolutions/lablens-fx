package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.Setting;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SettingRepository extends CrudRepository<Setting, Long> {

    Optional<Setting> findBySettingName(String name);

}
