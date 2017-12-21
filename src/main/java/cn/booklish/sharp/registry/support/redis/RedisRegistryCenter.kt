package cn.booklish.sharp.registry.support.redis

import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.exception.*
import cn.booklish.sharp.serialize.GsonUtil
import redis.clients.jedis.JedisPool

/**
 * @Author: liuxindong
 * @Description:  redis服务注册中心
 * @Created: 2017/12/21 14:03
 * @Modified:
 */
class RedisRegistryCenter (config: RedisConnectionConfig):RegistryCenter {


    private val pool = config.let {
        val split = it.address.split(":")
        JedisPool(config.poolConfig,split[0],split[1].toInt(),config.connectionTimeOut)
    }

    override fun getChildrenPath(path: String): Set<String> {
        val client = pool.resource
        try{
            return client.keys(path+"/*")
        } catch (e:Exception){
            throw GetChildrenPathException("redis-获取服务子节点列表失败",e)
        } finally {
            pool.returnResource(client)
        }
    }

    override fun getData(path: String): RegisterInfo {
        val client = pool.resource
        try{
            if(checkPathExists(path)){
                return GsonUtil.jsonToObject(client.get(path),RegisterInfo::class.java)
            }
            throw NoSuchPathException("redis-服务[$path]不存在")
        } catch (e:Exception){
            throw GetPathDataException("redis-获取服务[$path]的数据失败", e)
        } finally {
            pool.returnResource(client)
        }
    }

    override fun createPath(path: String, data: Any) {
        val client = pool.resource
        try{
            client.set(path,GsonUtil.objectToJson(data))
        } catch (e:Exception){
            throw CreatePathException("redis-创建服务[$path]失败", e)
        } finally {
            pool.returnResource(client)
        }
    }

    override fun updatePath(path: String, data: Any) {
        val client = pool.resource
        try{
            client.set(path,GsonUtil.objectToJson(data))
        } catch (e:Exception){
            throw UpdatePathException("redis-更新服务[$path]数据失败", e)
        } finally {
            pool.returnResource(client)
        }
    }

    override fun deletePath(path: String) {
        val client = pool.resource
        try{
            client.del(path)
        } catch (e:Exception){
            throw DeletePathException("redis-删除服务[$path]失败", e)
        } finally {
            pool.returnResource(client)
        }
    }

    override fun checkPathExists(path: String): Boolean {
        val client = pool.resource
        try{
            return client.exists(path)
        } catch (e:Exception){
            throw DeletePathException("redis-检查服务[$path]是否存在失败", e)
        } finally {
            pool.returnResource(client)
        }
    }


}