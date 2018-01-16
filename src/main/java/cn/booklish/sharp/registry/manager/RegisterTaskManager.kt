package cn.booklish.sharp.registry.manager

import cn.booklish.sharp.compute.RpcServiceBeanManager
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.config.RegistryConfig
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.min

/**
 * 注册任务管理器，统一管理服务提供者的服务注册行为
 */
object RegisterTaskManager{

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val exec = Executors.newFixedThreadPool(min(Runtime.getRuntime().availableProcessors()+1,32))

    private lateinit var registryConfig: RegistryConfig

    private lateinit var protocolConfig: ProtocolConfig

    fun init(registryConfig: RegistryConfig, protocolConfig:ProtocolConfig){
        this.registryConfig = registryConfig
        this.protocolConfig = protocolConfig
    }

    fun submit(registerInfo: RegisterInfo){
        exec.execute{
            val serviceName = (registerInfo.bean).javaClass.typeName
            try {
                //将服务信息注册到注册中心
                val registryCenter = registryConfig.registryCenter?: throw IllegalStateException("无效的注册中心,服务注册失败")
                val key = protocolConfig.name.value + serviceName + "?version=" + registerInfo.version
                registryCenter.register(key,registerInfo.address)
                logger.info("[Sharp] : 服务 $serviceName 注册成功, key = $key , value = ${registerInfo.address}")
                when(protocolConfig.name){
                    ProtocolName.RMI -> {}
                    ProtocolName.SHARP -> {
                        //服务端保存服务实体
                        RpcServiceBeanManager.add(registerInfo.bean)
                    }
                }
            } catch (e: Exception) {
                Thread.currentThread().interrupt()
                throw RuntimeException("[Sharp] : 服务 $serviceName 注册失败",e)
            }
        }
    }

    fun stop(){
        exec.shutdown()
    }


    class RegisterInfo(val bean:Any,val address:String,val version:String)
}