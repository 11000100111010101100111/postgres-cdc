package io.postgers.cdc.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.postgers.cdc.CdcFactory;
import io.postgers.cdc.CdcInstance;
import io.postgers.cdc.Source;
import io.postgers.cdc.config.PGConfig;
import io.postgers.cdc.entity.BaseReader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
class CdcFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(CdcFactoryTest.class);

    //@Test
    void test() {
        final PGConfig pgConfig = PGConfig.config()
                .host("localhost")
                .port(5432)
                .database("postgres")
                .username("postgres")
                .password("123456")
                .offset("cache" + File.separator + "dbhistory.dat");
        final BaseReader.Binder binder = BaseReader.Binder
                .on("postgres", "public", "your_database");
        try(final CdcInstance<PGConfig> cdcInstance = CdcFactory.createNew(Source.PG)) {
            cdcInstance.init(pgConfig);
            cdcInstance.whenInsert(binder.asNewDefaultReader(event -> {
                final JsonNode after = event.getAfter();
                log.info("Event insert, after: {}", after.toString());
            })).whenUpdate(binder.asNewDefaultReader(event -> {
                final JsonNode before = event.getBefore();
                final JsonNode after = event.getAfter();
                log.info("Event update, before: {}, after: {}", before.toString(), after.toString());
            })).whenDelete(binder.asNewDefaultReader(event -> {
                final JsonNode before = event.getBefore();
                log.info("Event delete, before: {}", before.toString());
            })).start();
            await();
        }
    }

    void await() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }
}