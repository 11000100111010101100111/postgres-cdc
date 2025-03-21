package io.postgers.cdc.entity.reader;

import com.fasterxml.jackson.databind.JsonNode;
import io.postgers.cdc.core.CDCEvent;
import io.postgers.cdc.core.ddl.DeleteEvent;
import io.postgers.cdc.core.ddl.InsertEvent;
import io.postgers.cdc.core.ddl.UpdateEvent;

import java.util.Optional;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public class SingleFieldReader<V, P extends CDCEvent> extends CacheByKey<V, P> {
    String tableField;

    public static <V, P extends CDCEvent> SingleFieldReader<V, P> of(String database, String schema, String table) {
        return new SingleFieldReader<>(database, schema, table);
    }

    public SingleFieldReader(String database, String schema, String table) {
        super(database, schema, table);
    }

    @Override
    public void process(CDCEvent event) {
        JsonNode value = null;
        if (event instanceof InsertEvent || event instanceof UpdateEvent) {
            value = event.getAfter();
        } else if (event instanceof DeleteEvent) {
            value = event.getBefore();
        }
        Optional.ofNullable(value)
                .map(map -> map.path(tableField).asText())
                .ifPresent(result -> {
            //@todo save to cache
                    System.out.println(result);
        });
    }

    public SingleFieldReader<V, P> withCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public SingleFieldReader<V, P> withMapping(String field) {
        this.tableField = field;
        return this;
    }
}
