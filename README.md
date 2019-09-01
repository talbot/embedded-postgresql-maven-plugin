# embedded-postgresql-maven-plugin
[![Build status](https://travis-ci.org/slavaz/embedded-postgresql-maven-plugin.svg?branch=master)](https://travis-ci.org/slavaz/embedded-postgresql-maven-plugin/)

## Description

Embedded PostgreSQL Maven Plugin provides a platform neutral way for running postgres server in integration tests.
This plugin  is based on [Embedded PostgreSQL Server library](https://github.com/yandex-qatools/postgresql-embedded)

## Configuration

### pgServerVersion

The plugin currently supports next PostgreSQL versions: 9.4.14, 9.5.15, 9.6.11, 10.6, 11.1. 
Default is the latest version.

You also may use aliases:
* 9.4 -> 9.4.14
* 9.5 -> 9.5.15
* 9.6 -> 9.6.11
* 10 -> 10.6
* 11 -> 11.1
* latest -> 11.1

### pgDatabaseDir

Where server files will be plased. Default {project.build.directory}/pgdata

### pgHost

Host(IP address) for listening incoming connections.

### pgPort

Port for listening incoming connections.

### pgLocale

Locale for embedded PostgreSQL server. Leave empty for running the server with system locale.
Specify "no" to skip locale & charset definition.

### pgCharset

Charset for embedded PostgreSQL server. Leave empty for running the server with system charset.
Specify "no" to skip locale & charset definition.

### startupTimeout

Timeout in milliseconds which we are waiting for Postgres server to start. 
Default is 15000 milliseconds (15 seconds).

### dbName

Database name. Will be created

### userName

User name. Will be created

### password

User password for newly created user

## Usage example

Starts PostgreSQL server, creates a database and a user with specified password

    ...
    <build>
        ..
        <plugins>
          <plugin>
            <groupId>com.github.slavaz</groupId>
            <artifactId>embedded-postgresql-maven-plugin</artifactId>
            <configuration>
              <pgServerVersion>9.5</pgServerVersion>
              <pgServerPort>15432</pgServerPort>
              <dbName>testdb</dbName>
              <userName>testuser</userName>
              <password>userpass</password>
            </configuration>
            <executions>
              <execution>
                <id>start-pgsql</id>
                <phase>pre-integration-test</phase>
                <goals>
                  <goal>start</goal>
                </goals>
              </execution>
              <execution>
                <id>stop-pgsql</id>
                <phase>post-integration-test</phase>
                <goals>
                  <goal>stop</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
    </build>

## Goals
                  
1. `start` -- start the server
2. `stop` -- stop the server
