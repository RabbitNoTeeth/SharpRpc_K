package `fun`.bookish.sharp.remoting.netty4.core

import `fun`.bookish.sharp.compute.ServiceImplManager
import `fun`.bookish.sharp.config.ServiceExport
import `fun`.bookish.sharp.manage.ProviderManageBean
import `fun`.bookish.sharp.manage.ProviderManager
import `fun`.bookish.sharp.model.RegisterValue
import `fun`.bookish.sharp.protocol.api.ProtocolName
import `fun`.bookish.sharp.remoting.netty4.codec.MessageCodec
import `fun`.bookish.sharp.remoting.netty4.handler.ServerChannelHandler
import `fun`.bookish.sharp.serialize.GsonUtil
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
        val originalServiceName = serviceExport.serviceInterface.typeName
        val serviceName = originalServiceName.replace(".","/",false)
        val registryCenters = serviceExport.registryCenters

        for (protocol in protocols) {
            //生成注册信息的键值
            val key = "SharpRpc://" + serviceName + "?version=" + serviceExport.version
            //生成注册信息的值
            var value: RegisterValue? = null

            when (protocol.name) {
                ProtocolName.RMI -> {
                    //生成服务的RMI绑定地址
                    val address = "rmi://${protocol.host}:${protocol.port}/$serviceName/version-${serviceExport.version}"
                    try {
                        //绑定服务
                        Naming.bind(address, serviceExport.serviceRef as Remote)
                        logger.info("successfully bind rmi service \"$originalServiceName\" , address=$address")
                        value = RegisterValue(protocol.name, address, protocol.weight)
                    } catch (e: Exception) {
                        val message = "failed bind rmi service \"$originalServiceName\" to the address \"$address\""
                        logger.error(message)
                        throw IllegalStateException(message, e)
                    }
                }
                ProtocolName.SHARP -> {
                    val address = "${protocol.host}:${protocol.port}"
                    try {
                        val f = this.bootstrap.clone().bind(protocol.port).sync()
                        ProviderManager.cache(ProviderManageBean(originalServiceName,address))
                        f.channel().closeFuture().addListener {
                            if(it.isDone && it.isSuccess){
                                ProviderManager.remove(address)
                            }
                        }
                        //服务端保存服务实现实体
                        ServiceImplManager.add(serviceExport.serviceInterface,serviceExport.serviceRef)
                        logger.info("successfully start a provider of service $originalServiceName, address=$address")
                        value = RegisterValue(protocol.name,address,protocol.weight)
                    } catch (e: Exception) {
                        val message = "failed start a provider of service \"$originalServiceName\", address=$address"
                        logger.error(message)
                        throw IllegalStateException(message, e)
                    }
                }
            }

            //将注册服务到注册中心
            for (registryCenter in registryCenters) {
                try {
                    registryCenter.register(key, GsonUtil.objectToJson(value))
                    logger.info("successfully registered service  \"$originalServiceName\" to [${registryCenter.address()}]," +
                            " key=$key, value=$value")
                }catch (e: Exception){
                    val message = "failed registered service  \"$originalServiceName\" to [${registryCenter.address()}]," +
                            " key=$key, value=$value"
                    logger.error(message)
                    throw IllegalStateException(message, e)
                }
            }

        }
    }

}