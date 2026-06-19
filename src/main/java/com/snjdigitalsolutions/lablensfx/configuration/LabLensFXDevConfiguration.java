package com.snjdigitalsolutions.lablensfx.configuration;

import com.snjdigitalsolutions.lablensfx.graph.DefaultGraphViewer;
import com.snjdigitalsolutions.lablensfx.graph.GraphViewer;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
@Profile("dev")
public class LabLensFXDevConfiguration {

    /**
     * Exposes an H2 TCP server bean so the embedded database can be inspected
     * externally during development.
     *
     * @return a started H2 TCP server
     * @throws SQLException if the server cannot bind to its port
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    /**
     * Repair-mode bean that runs H2 schema repair before the datasource is fully initialized.
     *
     * @return a {@link FlywayMigrationStrategy} that repairs then migrates
     */
    @Bean
    public FlywayMigrationStrategy repairFirst() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }

}
