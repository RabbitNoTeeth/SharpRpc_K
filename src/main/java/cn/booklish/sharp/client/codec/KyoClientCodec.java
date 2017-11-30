package cn.booklish.sharp.client.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * kyo编解码器
 */
public class KyoClientCodec extends CombinedChannelDuplexHandler<KyroClientDecoder,KyroClientEncoder> {

    public KyoClientCodec(){
        super(new KyroClientDecoder(),new KyroClientEncoder());
    }
}
