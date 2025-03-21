package io.postgers.cdc.entity.reader;

import io.postgers.cdc.core.CDCEvent;
import io.postgers.cdc.entity.BaseReader;

import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public abstract class CacheToLocal<V, P extends CDCEvent> extends BaseReader {
    protected Consumer<P> consumer;

    protected CacheToLocal(String database, String schema, String table) {
        super(database, schema, table);
    }

    public abstract CacheToLocal<V, P> withConsumer(Consumer<P> consumer);

    public Consumer<P> getConsumer() {
        return consumer;
    }
}
