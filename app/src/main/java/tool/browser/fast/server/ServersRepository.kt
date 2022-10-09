package tool.browser.fast.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tool.browser.fast.remote.ConfigManager
import tool.browser.fast.server.manager.ServerManager
import tool.browser.fast.utils.optListObj
import tool.browser.fast.utils.optListString
import tool.browser.fast.utils.toJsonObjectOrNull

object ServersRepository {
    private val mDefaultNodes by lazy {
        listOf(
            ServerNode(
                host = "79.133.124.13",
                port = 3711,
                password = "M1hN9hSpue0LR5Uu2RGI",
                method = "chacha20-ietf-poly1305",
                country = "United States",
                city = "Chicago"
            ),
            ServerNode(
                host = "192.99.42.21",
                port = 3711,
                password = "M1hN9hSpue0LR5Uu2RGI",
                method = "chacha20-ietf-poly1305",
                country = "Canada",
                city = "Montreal"
            )
        )
    }
    private val mFastCity by lazy { mutableListOf<String>() }
    private val mRemoteNodes by lazy { mutableListOf<ServerNode>() }

    fun initialize() {
        val serverManager = ServerManager.get()
        GlobalScope.launch(Dispatchers.IO) {
            mDefaultNodes.forEach {
                serverManager.writeToDatabase(it)
            }
        }
    }

    fun getSmartNodes(): List<ServerNode> {
        val fastCity = mFastCity.ifEmpty {
            ConfigManager
                .mFastCityJson
                .toJsonObjectOrNull()
                ?.optJSONArray(ConfigManager.FAST_CITY)
                ?.optListString()
                ?.apply {
                    if (isNotEmpty()) {
                        mFastCity.addAll(this)
                    }
                }
        }
        return getAllNodes().filter { fastCity.isNullOrEmpty() || fastCity.contains(it.city) }
    }

    fun getAllNodes(): List<ServerNode> {
        val remoteNodes = mRemoteNodes.ifEmpty {
            ConfigManager
                .mServerNodeJson
                .toJsonObjectOrNull()
                ?.optJSONArray(ConfigManager.SERVER_NODE)
                ?.optListObj {
                    ServerNode.from(it)
                }
                ?.apply {
                    if (isNotEmpty()) {
                        mRemoteNodes.addAll(this)
                    }
                }
        }
        return remoteNodes?.ifEmpty { mDefaultNodes } ?: mDefaultNodes
    }
}