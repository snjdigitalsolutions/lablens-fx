package com.snjdigitalsolutions.lablensfx.repository;

import com.snjdigitalsolutions.lablensfx.orm.Setting;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SettingRepository extends CrudRepository<Setting, Long> {

    /**
     * Looks up a setting by its unique name key.
     *
     * @param name the name of the setting to retrieve
     * @return an {@link Optional} containing the setting if found
     */
    Optional<Setting> findBySettingName(String name);

}
