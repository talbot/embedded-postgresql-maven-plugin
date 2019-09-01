package com.github.slavaz.maven.plugin.postgresql.embedded.psql;

import com.github.slavaz.maven.plugin.postgresql.embedded.psql.util.PostgresConfigUtil;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.embed.postgresql.Command;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.RuntimeConfigBuilder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by slavaz on 13/02/17.
 */
public class PgInstanceManager {

    private static final Logger log = LoggerFactory.getLogger(PgInstanceManager.class);

    private static final AtomicReference<PostgresProcess> process = new AtomicReference<>();

    public static void start(final IPgInstanceProcessData pgInstanceProcessData) {
        process.compareAndSet(null, createProcess(pgInstanceProcessData));
    }

    private static PostgresProcess createProcess(IPgInstanceProcessData pgInstanceProcessData) {
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.Postgres)
                .daemonProcess(true)
                .build();
        PostgresStarter<PostgresExecutable, PostgresProcess> postgresStarter = PostgresStarter.getInstance(runtimeConfig);
        final PostgresConfig postgresConfig;
        try {
            postgresConfig = PostgresConfigUtil.get(pgInstanceProcessData);
        } catch (IOException e) {
            log.error("Failed to get PostgreSQL config: error={}", e.getMessage());
            throw new RuntimeException(e);
        }
        PostgresExecutable postgresExecutable = postgresStarter.prepare(postgresConfig);
        try {
            return postgresExecutable.start();
        } catch (IOException e) {
            log.error("Cannot start Postgres: error={}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
        process.updateAndGet(process -> {
            process.stop();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("Cannot stop PostgreSQL - Interrupted: error={}", e.getMessage());
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
