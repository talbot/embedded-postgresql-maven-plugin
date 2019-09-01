package com.github.slavaz.maven.plugin.postgresql.embedded.psql;

import com.github.slavaz.maven.plugin.postgresql.embedded.psql.data.PgInstanceProcessData;

import java.io.IOException;

/**
 * Starts a PostgreSQL instance in a separate thread using a separate classloader. This is necessary, because the
 * Embedded PostgreSQL registers a shutdown hook which is run when the JVM shuts down, to shut down the process. When a
 * build fails, this plugin's "stop" goal is never run, so we rely on the shutdown hook to shut down PostgreSQL and
 * clean up resources. However, Maven plugins run in separate class loaders, which means the required classes to shut
 * down PostgreSQL are not available any more in the shutdown hook. By starting a thread with our own class loader,
 * we ensure that the classes are available during the shutdown hook.
 */
public class IsolatedPgInstanceManager {
    private final ClassLoader classLoader;

    public IsolatedPgInstanceManager(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void start(IPgInstanceProcessData data) throws IOException {
        Thread postgresThread = new Thread(() -> PgInstanceManager.start(data), "postgres-embedded");
        postgresThread.setContextClassLoader(classLoader);
        postgresThread.start();

        try {
            postgresThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Embedded Postgres thread was interrupted", e);
        }
    }

    public void stop() {
        PgInstanceManager.stop();
    }

    @SuppressWarnings("unused")
    public static void startPostgres(String pgServerVersion, String pgHost, int pgPort, String dbName, String userName, String password,
                                     String pgDatabaseDir, String pgLocale, String pgCharset, long startupTimeout) {
        PgInstanceManager.start(new PgInstanceProcessData(pgServerVersion, pgHost, pgPort, dbName, userName, password, pgDatabaseDir, pgLocale, pgCharset, startupTimeout));
    }

    @SuppressWarnings("unused")
    public static void stopPostgres() {
        PgInstanceManager.stop();
    }
}
