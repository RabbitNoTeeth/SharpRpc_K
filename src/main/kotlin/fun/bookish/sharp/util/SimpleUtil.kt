package `fun`.bookish.sharp.util

import java.net.InetSocketAddress


fun resolveAddress(address:String): InetSocketAddress {
    val list = address.split(":")
    return InetSocketAddress(list[0],list[1].toInt())
}

fun resolveAddress(address:InetSocketAddress): String {
    return "${address.hostString}:${address.port}"
}

