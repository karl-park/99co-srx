package sg.searchhouse.agentconnect.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

object JsonUtil {
    fun <T> parseListOrEmpty(string: String, listItemClass: Class<T>): List<T> {
        return try {
            parseList(string, listItemClass)
        } catch (e: JsonSyntaxException) {
            emptyList()
        }
    }

    fun <T> parseListOrNull(string: String, listItemClass: Class<T>): List<T>? {
        return try {
            parseList(string, listItemClass)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    // Convert string into list of your specified type
    @Throws(JsonSyntaxException::class)
    fun <T> parseList(string: String, listItemClass: Class<T>): List<T> {
        val listType = TypeToken.getParameterized(List::class.java, listItemClass).type
        return Gson().fromJson(string, listType)
    }

    fun parseMap(string: String): Map<String, String>? {
        val mapType: Type = object : TypeToken<Map<String, String>?>() {}.type
        return try {
            Gson().fromJson<Map<String, String>>(string, mapType)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun parseToMapFile(string: String): Map<String, File>? {
        val mapType: Type = object : TypeToken<Map<String, File>?>() {}.type
        return try {
            Gson().fromJson<Map<String, File>>(string, mapType)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun parseMapAny(string: String): Map<String, Any>? {
        val mapType: Type = object : TypeToken<Map<String, Any>?>() {}.type
        return try {
            Gson().fromJson<Map<String, Any>>(string, mapType)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun parseToJsonString(data: Any): String? {
        return try {
            Gson().toJson(data)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun <T> parseStringToClass(string: String, itemClass: Class<T>): T? {
        return try {
            Gson().fromJson(string, itemClass)
        } catch (ex: JsonSyntaxException) {
            null
        }
    }

}