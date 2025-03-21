package io.postgers.cdc.config;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:23
 * @description
 */
public class PGConfig extends CdcConfig {
    String host;
    int port;
    String username;
    String password;
    String database;
    String offset;

    public static PGConfig config() {
        return new PGConfig();
    }

    public PGConfig host(String host) {
        this.host = host;
        return this;
    }

    public PGConfig port(int port) {
        this.port = port;
        return this;
    }

    public PGConfig database(String database) {
        this.database = database;
        return this;
    }

    public PGConfig username(String username) {
        this.username = username;
        return this;
    }

    public PGConfig password(String password) {
        this.password = password;
        return this;
    }

    public PGConfig offset(String offset) {
        this.offset = offset;
        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getOffset() {
        return offset;
    }
}
