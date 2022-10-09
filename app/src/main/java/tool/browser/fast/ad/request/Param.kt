package tool.browser.fast.ad.request

import org.json.JSONObject

class Param(
    val source: String,
    val dataId: String,
    val type: String,
    val sort: Int
) {
    override fun toString(): String {
        return "[source=${source}, id=${dataId}, type=${type}, sort=${sort}]"
    }

    companion object {
        private const val FIELD_SOURCE = "fsa_so"
        private const val FIELD_DATA_ID = "fsa_dt"
        private const val FIELD_TYPE = "fsa_tp"
        private const val FIELD_SORT = "fsa_st"

        fun from(json: JSONObject): Param {
            return Param(
                source = json.optString(FIELD_SOURCE),
                dataId = json.optString(FIELD_DATA_ID),
                type = json.optString(FIELD_TYPE),
                sort = json.optInt(FIELD_SORT)
            )
        }
    }
}