package cn.booklish.sharp.client.codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:48
 * @desc: kyo编解码器
 */
public class ClientMessageCodec extends CombinedChannelDuplexHandler<ClientMessageDecoder,ClientMessageEncoder> {

    private static final Logger logger = Logger.getLogger(ClientMessageCodec.class);

    public ClientMessageCodec(){
        super(new ClientMessageDecoder(),new ClientMessageEncoder());
    }

}
