package com.snjdigitalsolutions.lablensfx.orm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "setting")
@Getter
@Setter
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "setting_name", length = 128)
    private String settingName;
    @Column(name = "boolean_value")
    private Boolean boolValue;
    @Column(name = "string_value", length = 128)
    private String stringValue;

}
