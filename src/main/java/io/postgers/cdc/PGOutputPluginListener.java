package io.postgers.cdc;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.postgers.cdc.config.CdcConfig;
import io.postgers.cdc.config.PGConfig;
import io.postgers.cdc.util.PrimaryParams;
import org.apache.kafka.connect.errors.IllegalWorkerStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
final class PGOutputPluginListener extends AbstractListener<PGConfig> {
    private final Logger logger = LoggerFactory.getLogger(PGOutputPluginListener.class);
    private final CdcInstance<? extends CdcConfig> cdcInstance;
    private DebeziumEngine<ChangeEvent<String, String>> engine;
    private Properties props;

    public PGOutputPluginListener(CdcInstance<? extends CdcConfig> cdcInstance) {
        if (null == cdcInstance) {
            throw new IllegalArgumentException("Empty CDC instance must not be create a cdc listener");
        }
        this.cdcInstance = cdcInstance;
    }

    @Override
    public final void init(PGConfig cdcConfig) {
        props = new Properties();
        final File file = new File(cdcConfig.getOffset());
        if (!file.exists() || !file.isFile()) {
            final File parentFile = file.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                throw new IllegalStateException("Unable mkdir " + parentFile.getPath() + " in file system");
            }
            try {
                Files.createFile(file.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        props.setProperty(PrimaryParams.NAME, PrimaryParams.POSTGRES_CONNECTOR);
        props.setProperty(PrimaryParams.CONNECTOR_CLASS, PrimaryParams.POSTGRES_CONNECTOR_CLASS_PATH);
        props.setProperty(PrimaryParams.DATABASE_HOSTNAME, cdcConfig.getHost());
        props.setProperty(PrimaryParams.DATABASE_PORT, String.valueOf(cdcConfig.getPort()));
        props.setProperty(PrimaryParams.DATABASE_USER, cdcConfig.getUsername());
        props.setProperty(PrimaryParams.DATABASE_PASSWORD, cdcConfig.getPassword());
        props.setProperty(PrimaryParams.DATABASE_DB_NAME, cdcConfig.getDatabase());
        props.setProperty(PrimaryParams.DATABASE_SERVER_NAME, "CDC-LINK-" + cdcInstance.getId());
        props.setProperty(PrimaryParams.PLUGIN_NAME, PrimaryParams.PG_OUTPUT);
        props.put(PrimaryParams.DATABASE_HISTORY, PrimaryParams.FILE_DATABASE_HISTORY_CLASS_PATH);
        props.put(PrimaryParams.OFFSET_STORAGE_FILE_FILENAME, cdcConfig.getOffset());
        props.put("snapshot.mode", "never");
    }

    @PostConstruct
    @Override
    public final void start() {
        if (null == props || props.isEmpty()) {
            throw new IllegalArgumentException("Lack of necessary startup parameters for cdc, invoke init method before start please");
        }
        if (null != engine) {
            stop();
            engine = null;
        }
        final String cdcTables = cdcInstance.cdcTables();
        if (cdcTables.isEmpty()) {
            logger.warn("No tables available for task listening were obtained. Please configure the listening policy and restart");
            return;
        }
        props.setProperty(PrimaryParams.TABLE_INCLUDE_LIST, cdcTables);
        engine = DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(cdcInstance::handleChangeEvent)
                .build();
        executor.execute(engine);
    }

    @PreDestroy
    @Override
    public final void stop() {
        if (engine != null) {
            try {
                engine.close();
            } catch (Exception e) {
                throw new IllegalWorkerStateException("Cdc flow stop failed, can not end with debezium: " + e.getMessage(), e);
            }
        }
    }
}