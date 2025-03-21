package io.postgers.cdc.config;

import io.postgers.cdc.CdcFactory;
import io.postgers.cdc.CdcInstance;
import io.postgers.cdc.Source;
import io.postgers.cdc.util.NumberUtil;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:23
 * @description
 */
public class CdcInstanceConfig {
    private static final String JDBC_MATCH = "jdbc:postgresql://([^:]+):(\\d+)/([^?]+)\\?(.*)";
    private String url;
    private String username;
    private String password;

    public CdcInstanceConfig(String connectionUrl, String username, String password) {
        setUrl(connectionUrl);
        setUsername(username);
        setPassword(password);
        checkUrl();
    }

    protected void checkUrl() {
        if (null == getUrl()) {
            throw new IllegalArgumentException("Postgres connect url is empty, set jdbc:postgresql://... for key pg.connection in system evn please");
        }
    }

    public CdcInstanceConfig() {
        setUrl(System.getProperty("pg.connection"));
        checkUrl();
        setUsername(System.getProperty("pg.username"));
        setPassword(System.getProperty("pg.password"));
    }

    public CdcInstance<PGConfig> masterPgCdcTemplate() {
        try {
            Pattern pattern = Pattern.compile(JDBC_MATCH);
            Matcher matcher = pattern.matcher(getUrl());
            if (matcher.find()) {
                String hostname = matcher.group(1);
                if (null == hostname) {
                    throw new IllegalArgumentException("Invalid database link, unable to find host");
                }
                String port = matcher.group(2);
                if (!NumberUtil.isInteger(port)) {
                    throw new IllegalArgumentException("Invalid database link, unable to find port");
                }
                String database = matcher.group(3);
                if (null == database) {
                    throw new IllegalArgumentException("Invalid database link, unable to find database");
                }
                final CdcInstance<PGConfig> cdcInstance = CdcFactory.createNew(Source.PG);
                final PGConfig pgConfig = PGConfig.config()
                        .host(hostname)
                        .port(Integer.parseInt(port))
                        .database(database)
                        .username(getUsername())
                        .password(getPassword())
                        .offset("cache" + File.separator + "dbhistory.dat");
                cdcInstance.init(pgConfig);
                return cdcInstance;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Initializing CDC template failed, can not get connection config from datasource", e);
        }
        throw new IllegalArgumentException("Initializing CDC template failed, can not get connection config from datasource");
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
