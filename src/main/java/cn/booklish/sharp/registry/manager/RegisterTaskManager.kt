package cn.booklish.sharp.registry.manager

import cn.booklish.sharp.compute.RpcServiceBeanManager
import cn.booklish.sharp.config.ServiceExport
import cn.booklish.sharp.model.RegisterValue
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.config.RegistryConfig
import cn.booklish.sharp.serialize.GsonUtil
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

    fun submit(serviceExport: ServiceExport<*>){

        exec.execute{

            val serviceName = serviceExport.serviceInterface.typeName.replace(".","/",false)

            try {

                val protocols = serviceExport.protocols

                val registryCenters = serviceExport.registryCenters

                for (protocol in protocols){
                    //生成注册信息的键值
                    val key = "SharpRpc://" + serviceName + "?version=" + serviceExport.version
                    //生成注册信息的值
                    var value:RegisterValue? = null

                    when(protocol.name){
                        ProtocolName.RMI -> {
                            //校验注册服务是否实现了Remote接口
                            if(serviceExport.serviceRef !is Remote){
                                throw IllegalArgumentException("服务类 $serviceName 未实现java.rmi.Remote接口,无法注册")
                            }
                            //创建RMI注册中心
                            LocateRegistry.createRegistry(protocol.port)
                            //生成服务的RMI绑定地址
                            val address = "rmi://${protocol.host}:${protocol.port}/$serviceName/version-${serviceExport.version}"
                            //绑定服务
                            Naming.bind(address, serviceExport.serviceRef as Remote)

                            value = RegisterValue(protocol.name,address)
                        }
                        ProtocolName.SHARP -> {
                            //服务端保存服务实体
                            RpcServiceBeanManager.add(serviceExport.serviceInterface,serviceExport.serviceRef)
                            val address = "${protocol.host}:${protocol.port}"
                            value = RegisterValue(protocol.name,address)
                        }
                    }

                    for(registryCenter in registryCenters){
                        registryCenter.register(key,GsonUtil.objectToJson(value))
                        logger.info("[Sharp] : 服务 $key 注册成功, value = $value")
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

}