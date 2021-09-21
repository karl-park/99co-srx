package sg.searchhouse.agentconnect

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.multidex.MultiDex
import com.pixplicity.easyprefs.library.Prefs
import javax.inject.Inject

class AgentConnectApplication @Inject constructor() : Application() {
    override fun onCreate() {
        super.onCreate()

        // Setup EasyPrefs
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //install multi dex
        MultiDex.install(base)
    }
}