package cn.booklish.sharp.client.codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:48
 * @desc: kyo编解码器
 */
public class KyoClientCodec extends CombinedChannelDuplexHandler<KyroClientDecoder,KyroClientEncoder> {

    private static final Logger logger = Logger.getLogger(KyoClientCodec.class);

    public KyoClientCodec(){
        super(new KyroClientDecoder(),new KyroClientEncoder());
    }

}
