package io.postgers.cdc.entity.reader;

import io.postgers.cdc.core.CDCEvent;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public class DefaultReader<V, P extends CDCEvent> extends CacheToLocal<V, P> {

    public static <V, P extends CDCEvent> DefaultReader<V, P> of(String database, String schema, String table) {
        return new DefaultReader<>(database, schema, table);
    }

    public DefaultReader(String database, String schema, String table) {
        super(database, schema, table);
    }

    @Override
    public void process(CDCEvent e) {
        Optional.ofNullable(this.consumer).ifPresent(c -> c.accept((P) e));
    }

    @Override
    public CacheToLocal<V, P> withConsumer(Consumer<P> consumer) {
        this.consumer = consumer;
        return this;
    }
}
