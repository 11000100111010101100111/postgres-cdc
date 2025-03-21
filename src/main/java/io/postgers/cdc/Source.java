package io.postgers.cdc;

import io.postgers.cdc.config.CdcConfig;

import java.util.function.Function;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public enum Source {
    PG(PGOutputPluginListener::new);

    Function<CdcInstance<? extends CdcConfig>, ? extends AbstractListener<? extends CdcConfig>> create;

    Source(Function<CdcInstance<? extends CdcConfig>, ? extends AbstractListener<? extends CdcConfig>> create) {
        this.create = create;
    }

    public <T extends AbstractListener<? extends CdcConfig>> T create(CdcInstance<? extends CdcConfig> instance) {
        return (T) create.apply(instance);
    }
}