package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.constant.SharpConstants
import io.netty.channel.Channel
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap


/**
 * @Author: liuxindong
 * @Description:  客户端channel连接管理器
 * @Created: 2017/12/13 8:47
 * @Modified:
 */
object ClientChannelManager{

    private val channelPoolMap = ConcurrentHashMap<InetSocketAddress, ChannelPoolFactory>()

    private var config: ChannelPoolConfig = ChannelPoolConfig()

    /**
     * 初始化channel连接池配置
     */
    fun init(config: ChannelPoolConfig = ChannelPoolConfig()){
        this.config = config
    }

    /**
     * 获取channelPool连接池
     */
    fun getChannelPool(serverAddress:InetSocketAddress): ChannelPoolFactory {
        val channelPool = channelPoolMap[serverAddress]
        if(channelPool==null){
            channelPoolMap.putIfAbsent(serverAddress, ChannelPoolFactory(serverAddress,config.poolConfig))
        }
        return channelPoolMap[serverAddress]!!
    }

}


/**
 * @Author: liuxindong
 * @Description:  channel连接池工厂
 * @Created: 2017/12/20 14:43
 * @Modified:
 */
class ChannelPoolFactory(address: InetSocketAddress,config: GenericObjectPoolConfig) {

    private val channelFactory = ChannelFactory(address)

    private val pool = GenericObjectPool<Channel>(channelFactory,config)

    fun getChannel():Channel{
        return pool.borrowObject()
    }

    fun releaseChannel(channel: Channel){
        try {
            pool.returnObject(channel)
        }catch (e:Exception){
            if(channel.isOpen){
                channel.close()
            }
        }
    }

}

/**
 * @Author: liuxindong
 * @Description:  channel工厂
 * @Created: 2017/12/20 15:24
 * @Modified:
 */
class ChannelFactory(private val address: InetSocketAddress): BasePooledObjectFactory<Channel>() {

    override fun create(): Channel {
        return Client.newChannel(address)
    }

    override fun validateObject(poolObject: PooledObject<Channel>): Boolean {
        poolObject.`object`?.let {
            if(it.isOpen && it.isActive){
                return true
            }
        }
        return false
    }

    override fun destroyObject(poolObject: PooledObject<Channel>) {
        poolObject.`object`?.let {
            if(it.isOpen){
                it.close()
            }
        }
    }

    override fun wrap(channel: Channel): PooledObject<Channel> {
        return DefaultPooledObject(channel)
    }

}

class ChannelPoolConfig{

    var timeout = SharpConstants.DEFAULT_CLIENT_CHANNEL_TIMEOUT

    val poolConfig = GenericObjectPoolConfig()

    init {
        poolConfig.testOnBorrow = true
        poolConfig.testOnReturn = true
    }
}
