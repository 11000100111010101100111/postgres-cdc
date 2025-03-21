package io.postgers.cdc.core.ddl;

import com.fasterxml.jackson.databind.JsonNode;
import io.postgers.cdc.core.CDCEvent;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:23
 * @description
 */
public class InsertEvent extends CDCEvent {
    JsonNode after;

    public InsertEvent() {
    }

    public InsertEvent(String database, String schema, String table) {
        super(database, schema, table);
    }

    @Override
    public JsonNode getAfter() {
        return after;
    }

    @Override
    public CDCEvent after(JsonNode after) {
        this.after = after;
        return this;
    }
}
