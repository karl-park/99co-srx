package sg.searchhouse.agentconnect.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.pixplicity.easyprefs.library.Prefs
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_EXISTING_HOME_REPORTS
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_SEARCH_HISTORY_LISTINGS
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_SEARCH_HISTORY_TRANSACTIONS
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_WHICH_SERVER
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.auth.GetConfigResponse
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.event.app.SignOutEvent

object SessionUtil {

    //Cookies
    fun isCookieValueIncluded(cookieString: String): Boolean {
        val cookieNameAndValuePair = cookieString.split(";").map { it.trim() }
        val cookie = cookieNameAndValuePair.firstOrNull()?.split("=")
        return !TextUtils.isEmpty(cookie?.getOrNull(1))
    }

    fun setCookies(cookies: HashSet<String>) {
        Prefs.putStringSet(SharedPreferenceKey.PREF_COOKIE, cookies)
    }

    fun getCookies(): HashSet<String> {
        return Prefs.getStringSet(
            SharedPreferenceKey.PREF_COOKIE,
            HashSet<String>()
        ) as HashSet<String>
    }

    private fun removeCookie() {
        Prefs.remove(SharedPreferenceKey.PREF_COOKIE)
    }

    fun removeLoginAgentCookie(): Boolean {
        //userSessionTokenImpersonated
        val originalCookies = getCookies()
        val tempCookies = HashSet(getCookies())
        tempCookies.removeAll { it.contains(ApiConstant.SESSION_KEY_LOGIN_AS_AGENT) }
        setCookies(tempCookies)
        return tempCookies.size != originalCookies.size
    }

    fun signOut(isUserAction: Boolean = false) {
        //Prevent keep calling sign out event when session time out
        val cookies = getCookies()
        if (cookies.isNotEmpty()) {
            RxBus.publish(SignOutEvent(isUserAction))
        }
    }

    fun removePrefs(context: Context) {
        removeCookie()
        removeUser()
        removeStreetSineUser()
        //removed street sine user auth from encrypted shared preferences
        removeStreetSineUserAuth(context)
        //removed portal auth from encrypted shared preferences
        removePortalAuthentication(context)
        removeQueryHistory()
        removeSubscriptionConfig()
        //remove auto import flag
        removeAutoImportListingsDialogShowFlag()
    }

    private fun removeQueryHistory() {
        Prefs.remove(PREF_SEARCH_HISTORY_LISTINGS)
        Prefs.remove(PREF_SEARCH_HISTORY_TRANSACTIONS)
        Prefs.remove(PREF_EXISTING_HOME_REPORTS)
    }

    fun removeCookieAndUserInfo() {
        removeCookie()
        removeUser()
        removeStreetSineUser()
    }

    fun setServer(serverInstance: Int) {
        Prefs.putInt(PREF_WHICH_SERVER, serverInstance)
    }

    private fun removeServer() {
        Prefs.remove(PREF_WHICH_SERVER)
    }

    fun setSubscriptionConfig(config: GetConfigResponse.Config) {
        val configString = JsonUtil.parseToJsonString(config) ?: return
        Prefs.putString(SharedPreferenceKey.PREF_USER_SUBSCRIPTION_CONFIG, configString)
    }

    @Throws(IllegalStateException::class, JsonSyntaxException::class)
    fun getSubscriptionConfig(): GetConfigResponse.Config {
        val configString =
            Prefs.getString(SharedPreferenceKey.PREF_USER_SUBSCRIPTION_CONFIG, null)
                ?: throw IllegalStateException("Missing `SharedPreferenceKey#PREF_USER_SUBSCRIPTION_CONFIG`")
        return Gson().fromJson(configString, GetConfigResponse.Config::class.java)
    }

    fun getSubscriptionConfigOrNull(): GetConfigResponse.Config? {
        return try {
            getSubscriptionConfig()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            null
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

    private fun removeSubscriptionConfig() {
        Prefs.remove(SharedPreferenceKey.PREF_USER_SUBSCRIPTION_CONFIG)
    }

    fun setAutoImportListingsDialogShowFlag(showDialog: Boolean) {
        //true -> show dialog
        //false -> never show dialog
        Prefs.putBoolean(SharedPreferenceKey.PREF_AUTO_IMPORT_LISTINGS_DIALOG_FLAG, showDialog)
    }

    fun getAutoImportListingsDialogShowFlag(): Boolean {
        return Prefs.getBoolean(SharedPreferenceKey.PREF_AUTO_IMPORT_LISTINGS_DIALOG_FLAG, true)
    }

    private fun removeAutoImportListingsDialogShowFlag() {
        Prefs.remove(SharedPreferenceKey.PREF_AUTO_IMPORT_LISTINGS_DIALOG_FLAG)
    }

    // Current user information
    @Throws(JsonSyntaxException::class)
    fun getCurrentUser(): UserPO? {
        val userString = Prefs.getString(SharedPreferenceKey.PREF_CURRENT_USER, null)
        return Gson().fromJson(userString, UserPO::class.java)
    }

    @Throws(JsonSyntaxException::class)
    fun setCurrentUser(currentUser: UserPO) {
        Prefs.putString(SharedPreferenceKey.PREF_CURRENT_USER, Gson().toJson(currentUser))
    }

    private fun removeUser() {
        Prefs.remove(SharedPreferenceKey.PREF_CURRENT_USER)
    }

    //SRX StreetSine user
    fun getStreetSineUser(): UserPO? {
        return try {
            val streetSineUser = Prefs.getString(SharedPreferenceKey.PREF_STREET_SINE_USER, null)
            Gson().fromJson(streetSineUser, UserPO::class.java)
        } catch (ex: JsonSyntaxException) {
            ex.printStackTrace()
            null
        }
    }

    @Throws(JsonSyntaxException::class)
    fun setStreetSineUser(user: UserPO) {
        Prefs.putString(SharedPreferenceKey.PREF_STREET_SINE_USER, Gson().toJson(user))
    }

    fun removeStreetSineUser() {
        Prefs.remove(SharedPreferenceKey.PREF_STREET_SINE_USER)
    }

    fun setDebugServerBaseUrl(url: String) {
        Prefs.putString(SharedPreferenceKey.PREF_DEBUG_BASE_SERVER_URL, url)
    }

    fun getDebugServerBaseUrl(): String? {
        return Prefs.getString(SharedPreferenceKey.PREF_DEBUG_BASE_SERVER_URL, null)
    }

    fun setStreetSineUserAuth(context: Context, email: String, pwd: String) {
        getEncryptedSharedPreferences(context).edit()
            .putString(SharedPreferenceKey.PREF_STREET_SINE_USER_EMAIL, email)
            .putString(SharedPreferenceKey.PREF_STREET_SINE_USER_PASSWORD, pwd)
            .apply()
    }

    fun getStreetSineUserEmail(context: Context): String? {
        return getEncryptedSharedPreferences(context).getString(
            SharedPreferenceKey.PREF_STREET_SINE_USER_EMAIL,
            null
        )
    }

    fun getStreetSineUserPassword(context: Context): String? {
        return getEncryptedSharedPreferences(context).getString(
            SharedPreferenceKey.PREF_STREET_SINE_USER_PASSWORD,
            null
        )
    }

    private fun removeStreetSineUserAuth(context: Context) {
        getEncryptedSharedPreferences(context).edit()
            .remove(SharedPreferenceKey.PREF_STREET_SINE_USER_EMAIL)
            .remove(SharedPreferenceKey.PREF_STREET_SINE_USER_PASSWORD).apply()
    }

    //Listing Management PORTAL Session
    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            SharedPreferenceKey.PREF_ENCRYPTED_SHARED_PREFERENCES,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Throws(JsonSyntaxException::class)
    fun setListingPortalMode(portalMode: PortalMode) {
        Prefs.putString(
            SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_MODE,
            Gson().toJson(portalMode)
        )
    }

    fun getListingPortalMode(): PortalMode? {
        val portalMode =
            Prefs.getString(SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_MODE, null)
        return Gson().fromJson(portalMode, PortalMode::class.java)
    }

    fun setPortalAuthRememberMe(rememberMe: Boolean) {
        Prefs.putBoolean(SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_REMEMBER_ME, rememberMe)
    }

    fun getPortalAuthRememberMe(): Boolean {
        return Prefs.getBoolean(
            SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_REMEMBER_ME,
            true
        )
    }

    // Authentication methods
    fun setPortalAuthentication(context: Context, email: String, pwd: String) {
        getEncryptedSharedPreferences(context).edit()
            .putString(SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_EMAIL, email)
            .putString(SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_PASSWORD, pwd)
            .apply()
    }

    fun getPortalAuthenticationEmail(context: Context): String? {
        return getEncryptedSharedPreferences(context).getString(
            SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_EMAIL,
            null
        )
    }

    fun getPortalAuthenticationPassword(context: Context): String? {
        return getEncryptedSharedPreferences(context).getString(
            SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_PASSWORD,
            null
        )
    }

    fun removePortalAuthentication(context: Context) {
        getEncryptedSharedPreferences(context).edit()
            .remove(SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_EMAIL)
            .remove(SharedPreferenceKey.PREF_LISTING_MANAGEMENT_PORTAL_PASSWORD)
            .apply()
    }
}