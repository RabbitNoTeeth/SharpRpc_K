package cn.booklish.sharp.server.codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.apache.log4j.Logger;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:55
 * @desc: 服务器编解码器
 */
public class ServerMessageCodec extends CombinedChannelDuplexHandler<ServerMessageDecoder,ServerMessageEncoder> {

    private static final Logger logger = Logger.getLogger(ServerMessageCodec.class);

    public ServerMessageCodec(){
        super(new ServerMessageDecoder(),new ServerMessageEncoder());
    }

}
