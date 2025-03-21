package io.postgers.cdc.core.ddl;

import io.postgers.cdc.core.CDCEvent;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:23
 * @description
 */
public class DeleteEvent extends CDCEvent {

    public DeleteEvent() {

    }

    public DeleteEvent(String database, String schema, String table) {
        super(database, schema, table);
    }
}
