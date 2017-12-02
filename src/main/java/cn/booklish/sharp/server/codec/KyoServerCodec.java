package cn.booklish.sharp.server.codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import org.apache.log4j.Logger;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:55
 * @desc: 服务器编解码器
 */
public class KyoServerCodec extends CombinedChannelDuplexHandler<KyroServerDecoder,KyroServerEncoder> {

    private static final Logger logger = Logger.getLogger(KyoServerCodec.class);

    public KyoServerCodec(){
        super(new KyroServerDecoder(),new KyroServerEncoder());
    }

}
