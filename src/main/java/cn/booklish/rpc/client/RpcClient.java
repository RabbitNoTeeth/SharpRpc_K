package cn.booklish.rpc.client;

import cn.booklish.rpc.server.model.RpcRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.codec.binary.Base64;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Author: liuxindong
 * @Description: Rpc客户端
 * @Create: 2017/11/22 10:04
 * @Modify:
 */
public class RpcClient {

    private static final Bootstrap b;

    static{
        b = new Bootstrap();
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
    }

    /**
     * 发送Rpc请求
     * @param host
     * @param port
     * @param rpcRequest
     * @param responseConsumer
     */
    public static void sendRpcRequest(String host, int port, RpcRequest rpcRequest, Consumer<Object> responseConsumer){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = b.clone();
            bootstrap.group(workerGroup)
                    .handler(new RpcClient.RpcPipelineInitializer(responseConsumer));
            ChannelFuture f = bootstrap.connect(host, port).sync();
            f.channel().writeAndFlush(rpcRequest);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("Rpc远程请求失败",e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Rpc请求响应处理器
     */
    static class RpcResponseHandler extends SimpleChannelInboundHandler<Object> {

        private final Consumer<Object> consumer;

        public RpcResponseHandler(Consumer<Object> consumer) {
            this.consumer = consumer;
        }

        protected void channelRead0(ChannelHandlerContext ctx, Object response) throws Exception {
            try{
                //自定义消息的处理方式
                consumer.accept(response);
            }finally {
                //消息处理完毕,关闭channel连接(由于Rpc调用多为一次性调用,处理完响应结果后,不需要保持连接)
                ctx.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    /**
     * Rpc客户端channelPipeline链
     */
    static class RpcPipelineInitializer extends ChannelInitializer<SocketChannel> {

        private final Consumer<Object> consumer;

        public RpcPipelineInitializer(Consumer<Object> consumer) {
            this.consumer = consumer;
        }

        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new RpcClient.KyroDecoder())
                    .addLast(new RpcClient.KyroEncoder())
                    .addLast(new RpcClient.RpcResponseHandler(consumer)
                    );
        }

    }

    /**
     * 解码器
     */
    static class KyroDecoder extends ByteToMessageDecoder {

        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
            //获取可读字节数
            int length = byteBuf.readableBytes();
            //分配一个新的数组来保存具有该长度的字节数据
            byte[] bytes = new byte[length];
            //将字节复制到该数组
            byteBuf.readBytes(bytes);
            Object rpcRequest = RpcClient.KryoUtil.readFromByteArray(bytes);
            list.add(rpcRequest);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    /**
     * 编码器
     */
    static class KyroEncoder extends MessageToByteEncoder<RpcRequest> {

        protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) {
            byteBuf.writeBytes(RpcClient.KryoUtil.writeObjectToByteArray(rpcRequest));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }

    /**
     * Kryo序列化工具类
     */
    static class KryoUtil {

        private static final String DEFAULT_ENCODING = "UTF-8";

        //每个线程的 Kryo 实例
        private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
            @Override
            protected Kryo initialValue() {
                Kryo kryo = new Kryo();

                /**
                 * 不要轻易改变这里的配置！更改之后，序列化的格式就会发生变化，
                 * 上线的同时就必须清除 Redis 里的所有缓存，
                 * 否则那些缓存再回来反序列化的时候，就会报错
                 */
                //支持对象循环引用（否则会栈溢出）
                kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置

                //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
                kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置

                //Fix the NPE bug when deserializing Collections.
                ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                        .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

                return kryo;
            }
        };

        /**
         * 获得当前线程的 Kryo 实例
         *
         * @return 当前线程的 Kryo 实例
         */
        public static Kryo getInstance() {
            return kryoLocal.get();
        }

        //-----------------------------------------------
        //          序列化/反序列化对象，及类型信息
        //          序列化的结果里，包含类型的信息
        //          反序列化时不再需要提供类型
        //-----------------------------------------------

        /**
         * 将对象【及类型】序列化为字节数组
         *
         * @param obj 任意对象
         * @param <T> 对象的类型
         * @return 序列化后的字节数组
         */
        public static <T> byte[] writeToByteArray(T obj) {
            ByteArrayOutputStream byteArrayOutputStream = null;
            Output output = null;
            try{
                byteArrayOutputStream = new ByteArrayOutputStream();
                output = new Output(byteArrayOutputStream);

                Kryo kryo = getInstance();
                kryo.writeClassAndObject(output, obj);
                output.flush();

                return byteArrayOutputStream.toByteArray();
            }finally {
                if(byteArrayOutputStream!=null){
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(output!=null){
                    output.close();
                }

            }

        }

        /**
         * 将对象【及类型】序列化为 String
         * 利用了 Base64 编码
         *
         * @param obj 任意对象
         * @param <T> 对象的类型
         * @return 序列化后的字符串
         */
        public static <T> String writeToString(T obj) {
            try {
                return new String(Base64.encodeBase64(writeToByteArray(obj)), DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * 将字节数组反序列化为原对象
         *
         * @param byteArray writeToByteArray 方法序列化后的字节数组
         * @param <T>       原对象的类型
         * @return 原对象
         */
        @SuppressWarnings("unchecked")
        public static <T> T readFromByteArray(byte[] byteArray) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            Input input = new Input(byteArrayInputStream);

            Kryo kryo = getInstance();
            return (T) kryo.readClassAndObject(input);
        }

        /**
         * 将 String 反序列化为原对象
         * 利用了 Base64 编码
         *
         * @param str writeToString 方法序列化后的字符串
         * @param <T> 原对象的类型
         * @return 原对象
         */
        public static <T> T readFromString(String str) {
            try {
                return readFromByteArray(Base64.decodeBase64(str.getBytes(DEFAULT_ENCODING)));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        //-----------------------------------------------
        //          只序列化/反序列化对象
        //          序列化的结果里，不包含类型的信息
        //-----------------------------------------------

        /**
         * 将对象序列化为字节数组
         *
         * @param obj 任意对象
         * @param <T> 对象的类型
         * @return 序列化后的字节数组
         */
        public static <T> byte[] writeObjectToByteArray(T obj) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);

            Kryo kryo = getInstance();
            kryo.writeObject(output, obj);
            output.flush();

            return byteArrayOutputStream.toByteArray();
        }

        /**
         * 将对象序列化为 String
         * 利用了 Base64 编码
         *
         * @param obj 任意对象
         * @param <T> 对象的类型
         * @return 序列化后的字符串
         */
        public static <T> String writeObjectToString(T obj) {
            try {
                return new String(Base64.encodeBase64(writeObjectToByteArray(obj)), DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        /**
         * 将字节数组反序列化为原对象
         *
         * @param byteArray writeToByteArray 方法序列化后的字节数组
         * @param clazz     原对象的 Class
         * @param <T>       原对象的类型
         * @return 原对象
         */
        @SuppressWarnings("unchecked")
        public static <T> T readObjectFromByteArray(byte[] byteArray, Class<T> clazz) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            Input input = new Input(byteArrayInputStream);

            Kryo kryo = getInstance();
            return kryo.readObject(input, clazz);
        }

        /**
         * 将 String 反序列化为原对象
         * 利用了 Base64 编码
         *
         * @param str   writeToString 方法序列化后的字符串
         * @param clazz 原对象的 Class
         * @param <T>   原对象的类型
         * @return 原对象
         */
        public static <T> T readObjectFromString(String str, Class<T> clazz) {
            try {
                return readObjectFromByteArray(Base64.decodeBase64(str.getBytes(DEFAULT_ENCODING)), clazz);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }
    }



}
