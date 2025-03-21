package io.postgers.cdc.entity.reader;

import io.postgers.cdc.core.CDCEvent;

import java.util.Optional;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public class CustomMappingReader<V, P extends CDCEvent> extends CacheByKey<V, P> {
    protected Process<V, P> process;
    private final static Process<Object, CDCEvent> DEFAULT = CDCEvent::getAfter;

    public static <V, P extends CDCEvent> CustomMappingReader<V, P> of(String database, String schema, String table) {
        return new CustomMappingReader<>(database, schema, table);
    }

    public CustomMappingReader(String database, String schema, String table) {
        super(database, schema, table);
    }

    @Override
    public void process(CDCEvent event) {
        Optional.ofNullable(this.process.process((P) event)).ifPresent(value -> {
            //@todo update cache key
            //...
            System.out.println(value.toString());
        });
    }

    @Override
    public CustomMappingReader<V, P> withCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public CustomMappingReader<V, P> withMapping(Process<V, P> process) {
        this.process = Optional.ofNullable(process).orElse((Process<V, P>) DEFAULT);
        return this;
    }
}
