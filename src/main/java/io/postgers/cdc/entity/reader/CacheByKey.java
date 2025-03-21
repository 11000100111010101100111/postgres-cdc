package io.postgers.cdc.entity.reader;

import io.postgers.cdc.core.CDCEvent;
import io.postgers.cdc.entity.BaseReader;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public abstract class CacheByKey<V, P extends CDCEvent> extends BaseReader {
    protected String cacheKey;

    protected CacheByKey(String database, String schema, String table) {
        super(database, schema, table);
    }

    public abstract CacheByKey<V, P> withCacheKey(String cacheKey);

    public String getCacheKey() {
        return cacheKey;
    }
}
