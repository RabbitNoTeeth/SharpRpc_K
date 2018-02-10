package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.compute.RpcServiceBeanManager
import cn.booklish.sharp.config.ServiceExport
import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.model.RegisterValue
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.remoting.netty4.handler.ServerChannelInitializer
import cn.booklish.sharp.serialize.GsonUtil
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
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

    init {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(ServerChannelInitializer(SharpConstants.DEFAULT_SERVER_CHANNEL_OPERATOR, SharpConstants.DEFAULT_SERIALIZER))
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
                        val message = "failed bind rmi service \"$originalServiceName\""
                        logger.error(message)
                        throw IllegalStateException(message, e)
                    }
                }
                ProtocolName.SHARP -> {
                    val address = "${protocol.host}:${protocol.port}"
                    try {
                        val f = this.bootstrap.clone().bind(protocol.port).sync()
                        f.channel().closeFuture().addListener {
                            workerGroup.shutdownGracefully()
                            bossGroup.shutdownGracefully()
                        }
                        //服务端保存服务实现实体
                        RpcServiceBeanManager.add(serviceExport.serviceInterface,serviceExport.serviceRef)
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