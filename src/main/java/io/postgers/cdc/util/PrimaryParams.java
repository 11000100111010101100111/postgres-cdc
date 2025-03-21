package io.postgers.cdc.util;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public class PrimaryParams {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HOSTNAME = "hostname";
    public static final String POST = "post";
    public static final String CONNECTOR_CLASS = "connector.class";
    public static final String DATABASE_HOSTNAME = "database.hostname";
    public static final String DATABASE_PORT = "database.port";
    public static final String DATABASE_USER = "database.user";
    public static final String DATABASE_PASSWORD = "database.password";
    public static final String DATABASE_DB_NAME = "database.dbname";
    public static final String DATABASE_SERVER_NAME = "database.server.name";
    public static final String PLUGIN_NAME = "plugin.name";
    public static final String PG_OUTPUT = "pgoutput";
    public static final String DATABASE_HISTORY = "database.history";
    public static final String OFFSET_STORAGE_FILE_FILENAME = "offset.storage.file.filename";
    public static final String TABLE_INCLUDE_LIST = "table.include.list";

    public static final String NAME = "name";
    public static final String POSTGRES_CONNECTOR = "postgres-connector";
    public static final String POSTGRES_CONNECTOR_CLASS_PATH = "io.debezium.connector.postgresql.PostgresConnector";
    public static final String FILE_DATABASE_HISTORY_CLASS_PATH = "io.debezium.storage.file.FileDatabaseHistory";
}
