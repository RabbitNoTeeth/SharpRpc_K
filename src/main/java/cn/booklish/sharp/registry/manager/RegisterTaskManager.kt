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
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.min


/**
 * 注册任务管理器，统一管理服务提供者的服务注册行为
 */
object RegisterTaskManager{

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val exec = Executors.newFixedThreadPool(min(Runtime.getRuntime().availableProcessors()+1,32))

    fun submit(serviceExport: ServiceExport<*>): Future<Boolean> {

        return exec.submit(Callable<Boolean>{

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
                            //创建RMI注册中心
                            LocateRegistry.createRegistry(protocol.port)
                            //生成服务的RMI绑定地址
                            val address = "rmi://${protocol.host}:${protocol.port}/$serviceName/version-${serviceExport.version}"
                            //绑定服务
                            Naming.bind(address, serviceExport.serviceRef as Remote)

                            value = RegisterValue(protocol.name,address,protocol.weight)
                        }
                        ProtocolName.SHARP -> {
                            //服务端保存服务实体
                            RpcServiceBeanManager.add(serviceExport.serviceInterface,serviceExport.serviceRef)
                            val address = "${protocol.host}:${protocol.port}"
                            value = RegisterValue(protocol.name,address,protocol.weight)
                        }
                    }

                    for(registryCenter in registryCenters){
                        registryCenter.register(key,GsonUtil.objectToJson(value))
                        logger.info("successfully registered service  \"${serviceExport.serviceInterface}\", [key=$key, value=$value]")
                    }

                }

                return@Callable true

            } catch (e: Exception) {
                Thread.currentThread().interrupt()
                throw RuntimeException("failed to register service \"${serviceExport.serviceInterface}\"",e)
            }

        })
    }

    fun stop(){
        exec.shutdown()
    }

}