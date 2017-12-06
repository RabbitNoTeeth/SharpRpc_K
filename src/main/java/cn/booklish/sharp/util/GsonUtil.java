package cn.booklish.sharp.util;

import com.google.gson.Gson;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @data: 2017/12/4 21:03
 * @desc:
 */
public class GsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object source){
        return gson.toJson(source);
    }

    public static <T> T jsonToObject(String json,Class<T> clazz){
        return gson.fromJson(json,clazz);
    }

}
