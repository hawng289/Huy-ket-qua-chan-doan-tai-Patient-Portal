package vn.com.itechcorp.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class FolderNameFactory {

    Map<String, String> map = new LinkedHashMap<>();

    public void add(String type, String folder) {
        String value = map.getOrDefault(type, null);
        if (value != null) return;
        map.put(type, folder);
    }

    public String get(String type) {
        return map.getOrDefault(type, null);
    }
}
