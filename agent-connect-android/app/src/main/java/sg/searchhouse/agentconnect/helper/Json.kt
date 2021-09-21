package sg.searchhouse.agentconnect.helper

import org.json.JSONObject

/**
 * Json builder
 *
 * Use case: `val response = Json { "result" to 1 }`
 */
class Json() {
    private val json = JSONObject()

    constructor(init: Json.() -> Unit) : this() {
        this.init()
    }

    infix fun String.to(value: Json) {
        json.put(this, value.json)
    }

    infix fun <T> String.to(value: T) {
        json.put(this, value)
    }

    override fun toString(): String {
        return json.toString()
    }
}