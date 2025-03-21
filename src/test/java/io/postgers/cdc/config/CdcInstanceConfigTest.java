package io.postgers.cdc.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.util.Optional;

class CdcInstanceConfigTest {

    @Nested
    class InstanceTest {

        @Test
        void testInstanceByParams() {
            Assertions.assertDoesNotThrow(() -> new CdcInstanceConfig("jdbc:postgresql://127.0.0.1:5432/postgres?schema=public", "root", "root"));
        }

        @Test
        void testInstanceUrlIsEmptyByParams() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> new CdcInstanceConfig(null, "root", "root"));
        }

        @Test
        void testInstanceBySystemEvn() {
            try {
                System.setProperty("pg.connection", "jdbc:postgresql://127.0.0.1:5432/postgres?schema=public");
                System.setProperty("pg.username", "root");
                System.setProperty("pg.password", "root");
                Assertions.assertDoesNotThrow((ThrowingSupplier<CdcInstanceConfig>) CdcInstanceConfig::new);
            } finally {
                System.clearProperty("pg.connection");
                System.clearProperty("pg.username");
                System.clearProperty("pg.password");
            }
        }

        @Test
        void testInstanceUrlIsEmptySystemEvn() {
            final String property = System.getProperty("pg.connection");
            try {
                System.clearProperty("pg.connection");
                System.setProperty("pg.username", "root");
                System.setProperty("pg.password", "root");
                Assertions.assertThrows(IllegalArgumentException.class, CdcInstanceConfig::new);
            } finally {
                System.clearProperty("pg.username");
                System.clearProperty("pg.password");
                Optional.ofNullable(property).ifPresent(value -> System.setProperty("pg.connection", value));
            }
        }


    }
}