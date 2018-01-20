package cn.booklish.sharp.registry.manager

import cn.booklish.sharp.compute.RpcServiceBeanManager
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.config.RegistryConfig
import org.apache.log4j.Logger
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.rmi.Naming
import java.rmi.Remote
import java.rmi.registry.LocateRegistry
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
            val serviceName = registerInfo.clazz.typeName.replace(".","/",false)
            try {
                val key = protocolConfig.name.value + "://" + serviceName + "?version=" + registerInfo.version

                when(protocolConfig.name){
                    ProtocolName.RMI -> {
                        if(registerInfo.bean !is Remote){
                            throw IllegalArgumentException("服务类 $serviceName 未实现java.rmi.Remote接口,无法注册")
                        }
                        LocateRegistry.createRegistry(protocolConfig.port)
                        val address = "rmi://${protocolConfig.host}:${protocolConfig.port}/$serviceName/version-${registerInfo.version}"
                        Naming.bind(address, registerInfo.bean)
                    }
                    ProtocolName.SHARP -> {
                        //服务端保存服务实体
                        RpcServiceBeanManager.add(registerInfo.clazz,registerInfo.bean)

                    }
                }

                registryConfig.registryCenter!!.register(key,registerInfo.address)
                logger.info("[Sharp] : 服务 $key 注册成功, value = ${registerInfo.address}")
            } catch (e: Exception) {
                Thread.currentThread().interrupt()
                throw RuntimeException("[Sharp] : 服务 $serviceName 注册失败",e)
            }
        }
    }

    fun stop(){
        exec.shutdown()
    }


    class RegisterInfo(val clazz:Class<*>,val bean:Any,val address:String,val version:String)
}