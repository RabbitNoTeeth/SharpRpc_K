package `fun`.bookish.sharp.remoting.netty4.core

import `fun`.bookish.sharp.config.ServiceExport
import `fun`.bookish.sharp.manage.bean.ServiceManager
import `fun`.bookish.sharp.manage.state.ProviderManageBean
import `fun`.bookish.sharp.manage.state.ProviderStateManager
import `fun`.bookish.sharp.model.RegisterValue
import `fun`.bookish.sharp.protocol.api.ProtocolName
import `fun`.bookish.sharp.remoting.netty4.codec.MessageCodec
import `fun`.bookish.sharp.remoting.netty4.handler.ServerChannelHandler
import `fun`.bookish.sharp.serialize.api.RpcSerializer
import `fun`.bookish.sharp.serialize.kryo.KryoSerializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.apache.log4j.Logger
import java.rmi.Naming
import java.rmi.Remote
import java.rmi.registry.LocateRegistry

/**
 * 服务端引导类
 */
object NettyServer {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()

    private val rpcSerializer:RpcSerializer = KryoSerializer()

    init {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        channel.pipeline().addLast(MessageCodec(rpcSerializer))
                                .addLast(ServerChannelHandler())
                    }
                })
    }

    /**
     * 启动Rpc服务器
     */
    fun start(serviceExport: ServiceExport<*>) {

        val protocols = serviceExport.protocols
        val serviceKey = serviceExport.serviceKey
        val registryCenters = serviceExport.registryCenters

        for (protocol in protocols) {
            //生成注册信息的键值
            val key = serviceExport.getRegisterKey()
            //生成注册信息的值
            var value: RegisterValue? = null

            when (protocol.name) {
                ProtocolName.RMI -> {
                    //生成服务的RMI绑定地址
                    val serviceName = serviceKey.replace(".","/")
                    val address = "rmi://${protocol.host}:${protocol.port}/$serviceName/version-${serviceExport.version}"
                    try {
                        //绑定服务
                        LocateRegistry.createRegistry(protocol.port)
                        Naming.bind(address, serviceExport.serviceRef as Remote)
                        logger.info("successfully bind rmi service \"$serviceKey\" , address=$address")
                        value = RegisterValue(protocol.name, address, protocol.weight)
                    } catch (e: Exception) {
                        val message = "failed bind rmi service \"$serviceKey\" to the address \"$address\""
                        logger.error(message)
                        throw IllegalStateException(message, e)
                    }
                }
                ProtocolName.SHARP -> {
                    val address = "${protocol.host}:${protocol.port}"
                    try {
                        val f = this.bootstrap.clone().bind(protocol.port).sync()
                        ProviderStateManager.cache(ProviderManageBean(serviceKey, address))
                        f.channel().closeFuture().addListener {
                            if(it.isDone && it.isSuccess){
                                ProviderStateManager.remove(address)
                            }
                        }
                        //服务端保存服务实现实体
                        ServiceManager.add(serviceKey,serviceExport.serviceInterface,serviceExport.serviceRef)
                        logger.info("successfully start a provider of service $serviceKey, address=$address")
                        value = RegisterValue(protocol.name,address,protocol.weight)
                    } catch (e: Exception) {
                        val message = "failed start a provider of service \"$serviceKey\", address=$address"
                        logger.error(message)
                        throw IllegalStateException(message, e)
                    }
                }
            }

            //将注册服务到注册中心
            for (registryCenter in registryCenters) {
                try {
                    registryCenter.register(key, value)
                    logger.info("successfully registered service  \"$serviceKey\" to [${registryCenter.address()}]," +
                            " key=$key, value=$value")
                }catch (e: Exception){
                    val message = "failed registered service  \"$serviceKey\" to [${registryCenter.address()}]," +
                            " key=$key, value=$value"
                    logger.error(message)
                    throw IllegalStateException(message, e)
                }
            }

        }
    }

}