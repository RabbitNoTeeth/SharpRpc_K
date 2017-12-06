package cn.booklish.sharp.register;


import cn.booklish.sharp.annotation.RpcService;
import cn.booklish.sharp.server.manage.ServerRpcRequestManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:52
 * @desc: Rpc服务扫描器
 */
public class RpcServiceAutoScanner {

    private static final Logger logger = Logger.getLogger(RpcServiceAutoScanner.class);

    private final String autoScanPath;

    private final String registerAddress;

    private final RegisterManager manager;

    public RpcServiceAutoScanner(String autoScanPath, String registerAddress,RegisterManager manager) {
        this.autoScanPath = autoScanPath;
        this.registerAddress = registerAddress;
        this.manager = manager;
        scan();
    }

    /**
     * 扫描需要注册的Rpc服务类,并且将注册信息提交到注册管理器
     */
    public void scan(){
        List<Class> serviceClasses = loadAllClassesByPackage(autoScanPath);
        serviceClasses.forEach(clazz -> {
            Annotation annotation = clazz.getAnnotation(RpcService.class);
            if(annotation!=null){
                String pathPrefix = ((RpcService) annotation).pathPrefix();
                RegisterBean entry = new RegisterBean(pathPrefix + "/" + clazz.getSimpleName()
                        , clazz.getTypeName(),registerAddress);
                manager.submit(entry);
            }
        });
    }


    /**
     * 读取指定package下的所有class文件
     * @param packageName
     */
    public List<Class> loadAllClassesByPackage(String packageName) {

        //存放所有class的list集合
        List<Class> classList = new ArrayList<Class>();

        // 是否循环搜索子包
        boolean recursive = true;

        //将包名转化为包路径
        String packagePath = packageName.replaceAll("\\.","/");

        Enumeration<URL> dir;

        try {
            //加载当前的包路径文件
            dir = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while(dir.hasMoreElements()){
                //获取加载的url
                URL url = dir.nextElement();
                //得到当前资源的类型,file或者jar
                String protocol = url.getProtocol();
                //如果是file,则加载的是当前的package包
                if(protocol.equals("file")){
                    //获取文件路径
                    String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
                    //遍历package下的所有class,包括其子包下的,添加到list中
                    findClassInPackage(packageName,filePath,recursive,classList);
                }
            }
        } catch (IOException e) {
            logger.error("服务扫描器: 加载package包失败");
            throw new RuntimeException(e);
        }

        return classList;
    }

    /**
     *  遍历package下的所有calss,包括其子包下的,添加到list中
     * @param packageName
     * @param filePath
     * @param recursive
     * @param classList
     */
    private void findClassInPackage(String packageName, String filePath, final boolean recursive, List<Class> classList) {
        File dir = new File(filePath);
        //如果file不存在或者不是目录,直接跳出
        if(!dir.exists() || !dir.isDirectory()){
            return;
        }
        File[] listFiles = dir.listFiles(file -> {
            //过滤出目录和class文件
            boolean acceptDir = recursive && file.isDirectory();
            boolean acceptClass = recursive && file.getName().endsWith(".class");
            return acceptDir || acceptClass;
        });
        for(File file:listFiles){
            if(file.isDirectory()){
                //如果还是目录,继续递归
                findClassInPackage(packageName+"."+file.getName(),file.getAbsolutePath(),recursive,classList);
            }else{
                //如果是class文件,获取文件名称,去掉后缀
                String className = file.getName().substring(0,file.getName().length()-6);
                try {
                    //加载class文件,添加到list中
                    classList.add(Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className));
                } catch (ClassNotFoundException e) {
                    logger.error("服务扫描器: 加载class文件失败");
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public String getAutoScanPath() {
        return autoScanPath;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }
}
