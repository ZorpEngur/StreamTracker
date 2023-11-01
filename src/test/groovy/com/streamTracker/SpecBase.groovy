package com.streamTracker

import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

class SpecBase extends Specification {

    PostgreSQLContainer postgres

    Flyway flyway

    def setupSpec() {
        this.postgres = new PostgreSQLContainer<>(
                "postgres:15-alpine"
        )
        this.postgres.withPassword("admin")
        this.postgres.withUsername("admin")
        this.postgres.withDatabaseName("test")
        this.postgres.setPortBindings(List.of("9999:5432"))
        this.postgres.start()
    }

    def setup() {
        this.flyway = Flyway.configure()
                .defaultSchema("stream_tracker")
                .driver("org.postgresql.Driver")
                .dataSource("jdbc:postgresql://localhost:9999/test", "admin", "admin")
                .cleanDisabled(false)
                .loggers("slf4j")
                .load();
        this.flyway.migrate();
    }

    def cleanup() {
        this.flyway.clean()
    }

    def cleanupSpec() {
        this.postgres.stop()
    }
}
