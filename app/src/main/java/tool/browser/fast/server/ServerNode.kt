package tool.browser.fast.server

import org.json.JSONObject

open class ServerNode(
    val host: String = "",
    val port: Int = 0,
    val method: String = "",
    val password: String = "",
    val country: String = "",
    val city: String = ""
) {
    open val nodeName: String
        get() {
            return "$country - $city"
        }

    override fun equals(other: Any?): Boolean {
        val otherNode = other as? ServerNode ?: return false
        return if (otherNode.nodeName == fast.nodeName) {
            this.nodeName == fast.nodeName
        } else {
            otherNode.host == host && otherNode.port == port
        }
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + port
        result = 31 * result + method.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + city.hashCode()
        return result
    }

    override fun toString(): String {
        return "[name=${nodeName}, host=${host}]"
    }

    companion object {
        val fast = object : ServerNode() {
            override val nodeName: String
                get() = "Fast Servers"
        }

        private const val FIELD_HOST = "fsf_ho"
        private const val FIELD_PORT = "fsf_po"
        private const val FIELD_METHOD = "fsf_mt"
        private const val FIELD_PASSWORD = "fsf_pw"
        private const val FIELD_COUNTRY = "fsf_cn"
        private const val FIELD_CITY = "fsf_ci"

        fun from(json: JSONObject): ServerNode {
            return ServerNode(
                host = json.optString(FIELD_HOST),
                port = json.optInt(FIELD_PORT),
                method = json.optString(FIELD_METHOD),
                password = json.optString(FIELD_PASSWORD),
                country = json.optString(FIELD_COUNTRY),
                city = json.optString(FIELD_CITY)
            )
        }
    }
}