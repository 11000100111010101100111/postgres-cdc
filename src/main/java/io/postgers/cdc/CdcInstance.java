package io.postgers.cdc;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.postgers.cdc.config.CdcConfig;
import io.postgers.cdc.core.CDCEvent;
import io.postgers.cdc.core.ddl.DeleteEvent;
import io.postgers.cdc.core.ddl.InsertEvent;
import io.postgers.cdc.core.ddl.UpdateEvent;
import io.postgers.cdc.entity.BaseReader;
import io.postgers.cdc.enums.CDCType;
import io.debezium.engine.ChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public final class CdcInstance<T extends CdcConfig> implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(CdcInstance.class);
    private final String id;
    private final Map<String, Map<String, Map<String, Map<CDCType, List<BaseReader>>>>> config;
    private AbstractListener<T> cdcListener;

    protected CdcInstance(String id) {
        this.config = new HashMap<>();
        this.id = id;
    }

    final CdcInstance<T> combineListener(AbstractListener<T> cdcListener) {
        this.cdcListener = cdcListener;
        return this;
    }

    public final String getId() {
        return this.id;
    }

    public final CdcInstance<T> whenInsert(BaseReader sourceReader) {
        return register(CDCType.INSERT, sourceReader);
    }

    public final CdcInstance<T> whenUpdate(BaseReader sourceReader) {
        return register(CDCType.UPDATE, sourceReader);
    }

    public final CdcInstance<T> whenDelete(BaseReader sourceReader) {
        return register(CDCType.DELETE, sourceReader);
    }

    public final CdcInstance<T> register(CDCType cdcType, BaseReader sourceReader) {
        config.computeIfAbsent(sourceReader.getDatabase(), key -> new HashMap<>())
                .computeIfAbsent(sourceReader.getSchema(), key -> new HashMap<>())
                .computeIfAbsent(sourceReader.getTable(), key -> new HashMap<>())
                .computeIfAbsent(cdcType, key -> new ArrayList<>())
                .add(sourceReader);
        return this;
    }

    public final String cdcTables() {
        final StringJoiner joiner = new StringJoiner(",");
        config.forEach((database, schemaMap) -> {
            schemaMap.forEach((schema, tableMap) -> {
                for (String table : tableMap.keySet()) {
                    joiner.add(String.format("%s.%s", schema, table));
                }
            });
        });
        return joiner.toString();
    }

    public final void init(T cdcConfig) {
        cdcListener.init(cdcConfig);
    }

    public final void start() {
        Optional.ofNullable(this.cdcListener).ifPresent(AbstractListener::start);
    }

    @Override
    public final void close() {
        Optional.ofNullable(this.cdcListener).ifPresent(AbstractListener::stop);
    }

    protected final void handleChangeEvent(ChangeEvent<String, String> changeEvent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode valueNode = mapper.readTree(changeEvent.value());
            final JsonNode payload = valueNode.path("payload");
            final JsonNode source = payload.path("source");
            final String db = source.path("db").asText();
            final String schema = source.path("schema").asText();
            final String table = source.path("table").asText();
            final Long txId = Optional.ofNullable(source.path("txId")).map(JsonNode::longValue).orElse(0L);
            final Long lsn = Optional.ofNullable(source.path("lsn")).map(JsonNode::longValue).orElse(0L);
            final Long ts = Optional.ofNullable(source.path("ts_ms")).map(JsonNode::longValue).orElse(0L);
            String operation = String.valueOf(payload.path("op").asText()).toLowerCase();

            final CDCType cdcType = CDCType.of(operation);

            final List<BaseReader> cdcReaders = Optional.ofNullable(config.get(db))
                    .map(map -> map.get(schema))
                    .map(map -> map.get(table))
                    .map(map -> map.get(cdcType))
                    .orElse(null);
            if (null == cdcReaders || cdcReaders.isEmpty()) {
                return;
            }
            Optional.ofNullable(event(cdcType, payload)).ifPresent(e -> {
                e.info(db, schema, table).time(ts).txId(txId).lsn(lsn);
                cdcReaders.forEach(reader -> {
                    try {
                        reader.process(e);
                    } catch (Exception ex) {
                        log.error("Fail to accept cdc data event {}, error message: {}", JSON.toJSONString(e), ex.getMessage());
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CDCEvent event(CDCType cdcType, JsonNode payload) {
        final JsonNode before = payload.path("before");
        final JsonNode after = payload.path("after");
        switch (cdcType) {
            case INSERT:
                return new InsertEvent().after(after).before(before);
            case UPDATE:
                return new UpdateEvent().after(after).before(before);
            case DELETE:
                return new DeleteEvent().after(after).before(before);
            default:
                log.warn("There is not support {} when cdc event occur now", cdcType);
        }
        return null;
    }
}