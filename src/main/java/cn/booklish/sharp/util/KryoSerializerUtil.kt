package cn.booklish.sharp.util

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.apache.commons.codec.binary.Base64
import org.objenesis.strategy.StdInstantiatorStrategy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * @Author: liuxindong
 * @Description:  kryo序列化工具类
 * @Created: 2017/12/13 9:05
 * @Modified:
 */
class KryoSerializerUtil {

    companion object{

        private val DEFAULT_ENCODING = "UTF-8"

        private val kryoLocal:ThreadLocal<Kryo> = object : ThreadLocal<Kryo>() {

            override fun initialValue(): Kryo {
                val kryo = Kryo()
                //支持对象循环引用（否则会栈溢出）
                kryo.references = true //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
                //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
                kryo.isRegistrationRequired = false //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
                //Fix the NPE bug when deserializing Collections.
                (kryo.instantiatorStrategy as Kryo.DefaultInstantiatorStrategy).fallbackInstantiatorStrategy = StdInstantiatorStrategy()
                return kryo
            }

        }

        /**
         * 获得当前线程的 Kryo 实例
         */
        fun getInstance(): Kryo {
            return kryoLocal.get()
        }

        /**
         * 将对象【及类型】序列化为字节数组
         */
        fun <T> writeToByteArray(obj: T): ByteArray {
            var byteArrayOutputStream: ByteArrayOutputStream? = null
            var output: Output? = null
            try {
                byteArrayOutputStream = ByteArrayOutputStream()
                output = Output(byteArrayOutputStream)
                val kryo = getInstance()
                kryo.writeClassAndObject(output, obj)
                output.flush()
                return byteArrayOutputStream.toByteArray()
            } finally {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close()
                }
                if (output != null) {
                    output.close()
                }
            }
        }

        /**
         * 将对象【及类型】序列化为 String
         * 利用了 Base64 编码
         */
        fun <T> writeToString(obj: T): String {
            return java.lang.String(Base64.encodeBase64(writeToByteArray(obj)), DEFAULT_ENCODING).toString()
        }

        /**
         * 将字节数组反序列化为原对象
         */
        fun <T> readFromByteArray(byteArray: ByteArray): T {
            val byteArrayInputStream = ByteArrayInputStream(byteArray)
            val input = Input(byteArrayInputStream)
            val kryo = getInstance()
            return kryo.readClassAndObject(input) as T
        }

        /**
         * 将 String 反序列化为原对象
         * 利用了 Base64 编码
         */
        fun <T> readFromString(str: String): T {
            return readFromByteArray(Base64.decodeBase64(str.toByteArray(charset(DEFAULT_ENCODING))))
        }

        /**
         * 将对象序列化为字节数组
         */
        fun <T> writeObjectToByteArray(obj: T): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val output = Output(byteArrayOutputStream)
            val kryo = getInstance()
            kryo.writeObject(output, obj)
            output.flush()
            return byteArrayOutputStream.toByteArray()
        }

        /**
         * 将对象序列化为 String
         * 利用了 Base64 编码
         */
        fun <T> writeObjectToString(obj: T): String {
            return java.lang.String(Base64.encodeBase64(writeObjectToByteArray(obj)), DEFAULT_ENCODING).toString()
        }

        /**
         * 将字节数组反序列化为原对象
         */
        fun <T> readObjectFromByteArray(byteArray: ByteArray, clazz: Class<T>): T {
            val byteArrayInputStream = ByteArrayInputStream(byteArray)
            val input = Input(byteArrayInputStream)
            val kryo = getInstance()
            return kryo.readObject(input, clazz)
        }

        /**
         * 将 String 反序列化为原对象
         * 利用了 Base64 编码
         */
        fun <T> readObjectFromString(str: String, clazz: Class<T>): T {
            return readObjectFromByteArray(Base64.decodeBase64(str.toByteArray(charset(DEFAULT_ENCODING))), clazz)
        }

    }

}