package sg.searchhouse.agentconnect.dsl

import com.google.gson.Gson
import java.io.Serializable

// TODO Write tests to check possible exceptions
fun <T> Serializable.toJsonString(classy: Class<T>): String = Gson().toJson(this, classy)