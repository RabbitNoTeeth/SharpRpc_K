package cn.booklish.sharp.registry.api

/**
 * @Author: liuxindong
 * @Description: 注册中心类型
 * @Created: 2017/12/20 9:43
 * @Modified:
 */
enum class RegistryCenterType(val value:String) {

    REDIS("redis"),ZOOKEEPER("zookeeper")

}