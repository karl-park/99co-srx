package sg.searchhouse.agentconnect.util

object ConfigUtil {
    fun isRunningTest(): Boolean {
        return try {
            Class.forName("sg.searchhouse.agentconnect.test.base.BaseActivityTest")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}