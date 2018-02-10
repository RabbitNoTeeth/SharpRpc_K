package `fun`.bookish.sharp.manage

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class ProviderManageBean(val serviceName: String,val serviceAddress: String) {
    val connections = AtomicInteger(0)
    val connectionsList = CopyOnWriteArrayList<String>()
}