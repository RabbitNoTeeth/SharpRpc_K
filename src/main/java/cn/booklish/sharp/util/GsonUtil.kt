package cn.booklish.sharp.util

import com.google.gson.Gson

/**
 * @Author: liuxindong
 * @Description:  json转化工具
 * @Created: 2017/12/13 9:05
 * @Modified:
 */
class GsonUtil {

    companion object{

        private val gson:Gson = Gson()

        fun objectToJson(obj: Any):String{
            return gson.toJson(obj)
        }

        fun <T> jsonToObject(json:String,clazz:Class<T>):T{
            return gson.fromJson(json,clazz)
        }
    }

}