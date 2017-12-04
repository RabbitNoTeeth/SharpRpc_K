package cn.booklish.sharp.client.codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:48
 * @desc: kyo编解码器
 */
public class ClientMessageCodec extends CombinedChannelDuplexHandler<LengthFieldBasedFrameDecoder,LengthFieldPrepender> {


    public ClientMessageCodec(){
        super(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4)
                ,new LengthFieldPrepender(4, false));
    }

}
