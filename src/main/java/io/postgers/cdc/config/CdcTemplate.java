package io.postgers.cdc.config;

import io.postgers.cdc.CdcInstance;

import java.util.Optional;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:23
 * @description
 */
public class CdcTemplate implements AutoCloseable {
    final CdcInstance<PGConfig> masterPgCdcTemplate;

    public CdcTemplate(CdcInstance<PGConfig> masterPgCdcTemplate) {
        this.masterPgCdcTemplate = masterPgCdcTemplate;
    }

    public void start() throws Exception {
        masterPgCdcTemplate.start();
    }

    public void close() throws Exception {
        Optional.ofNullable(masterPgCdcTemplate).ifPresent(CdcInstance::close);
    }
}
