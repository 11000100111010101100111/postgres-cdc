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
public interface ChangeFlower {
    void handelAdd(JsonNode after);

    void handelUpdate(JsonNode before, JsonNode after);

    void handelDelete(JsonNode before);
}
