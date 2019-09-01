package com.github.slavaz.maven.plugin.postgresql.embedded.psql.util;

import com.github.slavaz.maven.plugin.postgresql.embedded.psql.IPgInstanceProcessData;
import com.github.slavaz.maven.plugin.postgresql.embedded.psql.PgVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.net.InetAddress.getLocalHost;

public final class PostgresConfigUtil {

    private static final Log log = new SystemStreamLog();
    private static final Set<String> LOCAL_HOSTS = new HashSet<>();

    static {
        LOCAL_HOSTS.add("");
        LOCAL_HOSTS.add("localhost");
        LOCAL_HOSTS.add("127.0.0.1");
    }

    public static PostgresConfig get(@Nonnull IPgInstanceProcessData configData) throws IOException {
        AbstractPostgresConfig.Storage storage = getStorage(configData);
        AbstractPostgresConfig.Credentials credentials = getCredentials(configData);
        IVersion version = getVersion(configData);
        PostgresConfig config = getConfig(configData, storage, credentials, version);

        config.getAdditionalInitDbParams().addAll(getCharsetParameters(configData));
        log.info("Using Postgres configuration: " + config);
        return config;
    }

    private static Collection<String> getCharsetParameters(final IPgInstanceProcessData pgInstanceProcessData) {
        return new CharsetParameterList(pgInstanceProcessData).get();
    }

    @Nonnull
    private static PostgresConfig getConfig(@Nonnull IPgInstanceProcessData pgInstanceProcessData, final
    AbstractPostgresConfig.Storage storage, final AbstractPostgresConfig.Credentials creds, final IVersion version)
            throws IOException {
        return new PostgresConfig(version, getNet(pgInstanceProcessData), storage, new AbstractPostgresConfig
                .Timeout(pgInstanceProcessData.getStartupTimeout()), creds);
    }

    @Nonnull
    private static AbstractPostgresConfig.Credentials getCredentials(@Nonnull IPgInstanceProcessData processData) {
        return new AbstractPostgresConfig.Credentials(processData.getUserName(), processData.getPassword());
    }

    @Nonnull
    private static AbstractPostgresConfig.Storage getStorage(@Nonnull IPgInstanceProcessData processData) throws
            IOException {
        return new AbstractPostgresConfig.Storage(processData.getDbName(), processData.getPgDatabaseDir());
    }

    private static IVersion getVersion(final IPgInstanceProcessData pgInstanceProcessData) {
        return PgVersion.get(pgInstanceProcessData.getPgServerVersion());
    }

    @Nonnull
    private static AbstractPostgresConfig.Net getNet(@Nonnull IPgInstanceProcessData configurationData) throws
            IOException {
        String hostName = configurationData.getPgHost();
        if (hostName == null || LOCAL_HOSTS.contains(hostName)) {
            return new AbstractPostgresConfig.Net(getLocalHost().getHostAddress(), configurationData.getPgPort());
        }
        return new AbstractPostgresConfig.Net(configurationData.getPgHost(), configurationData.getPgPort());
    }
}
