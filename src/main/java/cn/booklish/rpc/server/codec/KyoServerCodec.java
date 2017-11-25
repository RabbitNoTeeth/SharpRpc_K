package cn.booklish.rpc.server.codec;

import io.netty.channel.CombinedChannelDuplexHandler;


/**
 * kyo编解码器
 */
public class KyoServerCodec extends CombinedChannelDuplexHandler<KyroServerDecoder,KyroServerEncoder> {

    public KyoServerCodec(){
        super(new KyroServerDecoder(),new KyroServerEncoder());
    }
}
