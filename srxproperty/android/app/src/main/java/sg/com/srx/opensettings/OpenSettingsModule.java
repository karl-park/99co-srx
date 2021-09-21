package sg.com.srx.opensettings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class OpenSettingsModule extends ReactContextBaseJavaModule {

    @Override
    public String getName() {
        /**
         * return the string name of the NativeModule which represents this class in JavaScript
         * In JS access this module through React.NativeModules.OpenSettings
         */
        return "OpenSettings";
    }

    @ReactMethod
    public void openNetworkSettings(Callback cb) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            cb.invoke(false);
            return;
        }
//new Intent(android.provider.Settings.ACTION_SETTINGS)
        try {

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", "sg.com.srx", null));
            currentActivity.startActivity(intent);

            cb.invoke(true);
        } catch (Exception e) {
            cb.invoke(e.getMessage());
        }
    }

    /* constructor */
    public OpenSettingsModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}