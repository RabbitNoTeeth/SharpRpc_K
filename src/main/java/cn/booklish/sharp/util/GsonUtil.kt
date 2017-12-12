package cn.booklish.sharp.util

import com.google.gson.Gson

/**
 * json转化工具
 */
class GsonUtil {

    companion object{

        val gson:Gson = Gson()

        fun objectToJson(obj: Any):String{
            return gson.toJson(obj)
        }

        fun <T> jsonToObject(json:String,clazz:Class<T>):T{
            return gson.fromJson(json,clazz)
        }
    }

}