package io.postgers.cdc;

import io.postgers.cdc.config.CdcConfig;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
abstract class AbstractListener<P extends CdcConfig> {
    protected final Executor executor;

    public AbstractListener() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void init(P config) {

    }

    public abstract void start();

    public abstract void stop();
}
