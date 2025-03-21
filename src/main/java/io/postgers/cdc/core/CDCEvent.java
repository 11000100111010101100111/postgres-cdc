package io.postgers.cdc.core;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public abstract class CDCEvent {
    String database;
    String schema;
    String table;
    Long time;
    Long txId;
    Long lsn;
    JsonNode before;

    public CDCEvent() {

    }

    public CDCEvent(String database, String schema, String table) {
        this.database = database;
        this.schema = schema;
        this.table = table;
    }
    public CDCEvent info(String database, String schema, String table) {
        this.database = database;
        this.schema = schema;
        this.table = table;
        return this;
    }

    public CDCEvent time(long time) {
        this.time = time;
        return this;
    }

    public CDCEvent txId(long txId) {
        this.txId = txId;
        return this;
    }

    public CDCEvent lsn(long lsn) {
        this.lsn = lsn;
        return this;
    }

    public JsonNode getBefore() {
        return before;
    }

    public JsonNode getAfter() {
        return null;
    }

    public CDCEvent before(JsonNode before) {
        this.before = before;
        return this;
    }

    public CDCEvent after(JsonNode after) {
        return this;
    }
}
