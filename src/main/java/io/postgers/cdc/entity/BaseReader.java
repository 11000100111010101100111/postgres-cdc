package io.postgers.cdc.entity;

import io.postgers.cdc.core.CDCEvent;
import io.postgers.cdc.entity.reader.CustomMappingReader;
import io.postgers.cdc.entity.reader.DefaultReader;
import io.postgers.cdc.entity.reader.SingleFieldReader;

import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public abstract class BaseReader {
    protected int type;
    protected String database;
    protected String schema;
    protected String table;

    protected BaseReader(String database, String schema, String table) {
        this.database = database;
        this.schema = schema;
        this.table = table;
    }

    public static <V, P extends CDCEvent> SingleFieldReader<V, P> single(String database, String schema, String table) {
        return SingleFieldReader.of(database, schema, table);
    }

    public static <V, P extends CDCEvent> CustomMappingReader<V, P> multi(String database, String schema, String table) {
        return CustomMappingReader.of(database, schema, table);
    }

    public static <V, P extends CDCEvent> DefaultReader<V, P> tolerant(String database, String schema, String table) {
        return DefaultReader.of(database, schema, table);
    }

    public String getDatabase() {
        return database;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public static interface Process<V, P extends CDCEvent> {
        public V process(P param);
    }

    public abstract void process(CDCEvent event);

    public static class Binder {
        String database;
        String schema;
        String table;

        Binder() {
        }

        public static Binder on(String db, String schema, String table) {
            final Binder binder = new Binder();
            binder.database = db;
            binder.schema = schema;
            binder.table = table;
            return binder;
        }

        public <V, P extends CDCEvent> SingleFieldReader<V, P> asNewSingleReader(String cacheKey, String tableField) {
            final SingleFieldReader<V, P> single = single(database, schema, table);
            return single.withCacheKey(cacheKey)
                    .withMapping(tableField);
        }

        public <V, P extends CDCEvent> CustomMappingReader<V, P> asNewCustomReader(String cacheKey, Process<V, P> process) {
            final CustomMappingReader<V, P> custom = multi(database, schema, table);
            return custom.withCacheKey(cacheKey)
                    .withMapping(process);
        }

        public <V, P extends CDCEvent> DefaultReader<V, P> asNewDefaultReader(Consumer<P> consumer) {
            final DefaultReader<V, P> custom = tolerant(database, schema, table);
            custom.withConsumer(consumer);
            return custom;
        }
    }
}
