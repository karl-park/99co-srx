package sg.com.srx;

import android.content.Intent;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.reactnativenavigation.NavigationActivity;
import com.wix.reactnativenotifications.core.InitialNotificationHolder;
import com.wix.reactnativenotifications.core.notification.PushNotificationProps;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import org.devio.rn.splashscreen.SplashScreen;

import java.util.Objects;

public class MainActivity extends NavigationActivity {

    private static final String KEY_GOOGLE_MESSAGE_ID = "google.message_id";
    private static final String KEY_NOTIFICATION_TYPE = "type";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.show(this);
        super.onCreate(savedInstanceState);
        handleBundle();
    }

    private void handleBundle() {
        try {
            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                //note: bundle could be different types.
                //so checking bundle is notification bundle or not.
                //set only notification bundle in initial notification holder when app is in background state
                if (isNotificationBundle(bundle)) {
                    PushNotificationProps params = new PushNotificationProps(bundle);
                    InitialNotificationHolder.getInstance().set(params);
                    Log.e("FCM_Notification", params + "setting params in notification holder");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean isNotificationBundle(Bundle bundle) {
        return bundle.getString(KEY_GOOGLE_MESSAGE_ID, null) != null &&
                bundle.getString(KEY_NOTIFICATION_TYPE, null) != null;
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
//    @Override
//    protected String getMainComponentName() {
//        return "SRXProperty";
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                PushNotificationProps notification = new PushNotificationProps(bundle);
                Object result = Arguments.fromBundle(notification.asBundle());
                final ReactInstanceManager instanceManager = ((ReactApplication) getApplicationContext()).getReactNativeHost().getReactInstanceManager();
                Objects.requireNonNull(instanceManager.getCurrentReactContext())
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("FCMNotificationNewIntent", result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
