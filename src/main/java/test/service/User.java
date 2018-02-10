package test.service;

import java.io.Serializable;

/**
 * @author Don9
 * @create 2018-01-16-13:43
 **/
public class User implements Serializable {

    private final String username = "liuxindong";

    private final int id;

    public User(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[id="+id+",name="+username+"]";
    }
}
