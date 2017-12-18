package cn.booklish.sharp.registry.api

import cn.booklish.sharp.annotation.RpcService
import org.apache.log4j.Logger
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.util.*

/**
 * @Author: liuxindong
 * @Description:  Rpc服务自动扫描器,自动扫描被标记为Rpc服务的类并发布到zookeeper
 * @Created: 2017/12/13 9:02
 * @Modified:
 */
class RpcServiceAutoScanner(private val autoScanPath:String, private val registerAddress:String) {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    /**
     * 扫描需要注册的Rpc服务类,并且将注册信息提交到注册管理器
     */
    fun scan() {
        val serviceClasses = loadAllClassesByPackage(autoScanPath)
        serviceClasses.forEach { clazz ->
            val annotation = clazz.getAnnotation(RpcService::class.java)
            if (annotation != null) {
                val path = (annotation as RpcService).path
                val registerInfo = RegisterInfo(path, clazz.typeName, registerAddress)
                RegisterTaskManager.submit(registerInfo)
            }
        }
    }


    /**
     * 读取指定package下的所有class文件
     * @param packageName
     */
    private fun loadAllClassesByPackage(packageName: String): List<Class<*>> {

        //存放所有class的list集合
        val classList = ArrayList<Class<*>>()

        // 是否循环搜索子包
        val recursive = true

        //将包名转化为包路径
        val packagePath = packageName.replace("\\.".toRegex(), "/")

        val dir: Enumeration<URL>

        try {
            //加载当前的包路径文件
            dir = Thread.currentThread().contextClassLoader.getResources(packagePath)
            while (dir.hasMoreElements()) {
                //获取加载的url
                val url = dir.nextElement()
                //得到当前资源的类型,file或者jar
                val protocol = url.protocol
                //如果是file,则加载的是当前的package包
                if (protocol == "file") {
                    //获取文件路径
                    val filePath = URLDecoder.decode(url.file, "UTF-8")
                    //遍历package下的所有class,包括其子包下的,添加到list中
                    findClassInPackage(packageName, filePath, recursive, classList)
                }
            }
        } catch (e: IOException) {
            logger.error("服务扫描器: 加载package包失败")
            throw RuntimeException(e)
        }

        return classList
    }

    /**
     * 遍历package下的所有calss,包括其子包下的,添加到list中
     */
    private fun findClassInPackage(packageName: String, filePath: String, recursive: Boolean, classList: MutableList<Class<*>>) {
        val dir = File(filePath)
        //如果file不存在或者不是目录,直接跳出
        if (!dir.exists() || !dir.isDirectory) {
            return
        }
        val listFiles = dir.listFiles { file ->
            //过滤出目录和class文件
            val acceptDir = recursive && file.isDirectory
            val acceptClass = recursive && file.name.endsWith(".class")
            acceptDir || acceptClass
        }
        for (file in listFiles!!) {
            if (file.isDirectory) {
                //如果还是目录,继续递归
                findClassInPackage(packageName + "." + file.name, file.absolutePath, recursive, classList)
            } else {
                //如果是class文件,获取文件名称,去掉后缀
                val className = file.name.substring(0, file.name.length - 6)
                try {
                    //加载class文件,添加到list中
                    classList.add(Thread.currentThread().contextClassLoader.loadClass(packageName + "." + className))
                } catch (e: ClassNotFoundException) {
                    logger.error("服务扫描器: 加载class文件失败")
                    throw RuntimeException(e)
                }

            }
        }

    }
}