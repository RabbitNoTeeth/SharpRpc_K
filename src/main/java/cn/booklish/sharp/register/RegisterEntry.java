package cn.booklish.sharp.register;

import java.util.Map;

public class RegisterEntry implements Map.Entry<String,String> {

    private final String key;

    private String value;

    public RegisterEntry(String key, String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        this.value = value;
        return this.value;
    }

    @Override
    public String toString() {
        return "["+this.key+","+this.value+"]";
    }
}
