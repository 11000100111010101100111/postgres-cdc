package io.postgers.cdc.listener;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Author xjh
 * @email gavinxiao@gmail.com
 * @github https://github.com/11000100111010101100111
 * @Date 2025/3/21 23:25
 * @description
 */

public class ListenData {
    List<String> tables;
    Map<String, List<String>> column;

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public Map<String, List<String>> getColumn() {
        return column;
    }

    public void setColumn(Map<String, List<String>> column) {
        this.column = column;
    }
}
