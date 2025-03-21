package io.postgers.cdc;

import io.postgers.cdc.config.CdcConfig;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */
public final class CdcFactory {
    private static final ConcurrentHashMap<String, CdcInstance<? extends CdcConfig>> factories = new ConcurrentHashMap<>(2 << 2 << 1);
    private static final ConcurrentHashMap<CdcInstance<? extends CdcConfig>, ? extends AbstractListener<? extends CdcConfig>> listeners = new ConcurrentHashMap<>(2 << 2 << 1);

    public static <T extends CdcConfig> CdcInstance<T> createNew(Source sourceType) {
        return computeIfAbsent(UUID.randomUUID().toString(), sourceType);
    }

    public static <T extends CdcConfig> CdcInstance<T> computeIfAbsent(String id, Source sourceType) {
        if (null == id || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        final CdcInstance<? extends CdcConfig> cdcInstance = factories.computeIfAbsent(id, CdcInstance::new);
        final AbstractListener<? extends CdcConfig> cdcListener = listeners.computeIfAbsent(cdcInstance, sourceType::create);
        return (CdcInstance<T>) cdcInstance.combineListener((AbstractListener) cdcListener);
    }
}
