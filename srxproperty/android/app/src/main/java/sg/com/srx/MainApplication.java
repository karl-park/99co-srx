package sg.com.srx;

import android.util.Log;

//import com.facebook.CallbackManager;
import com.facebook.react.PackageList;
import com.wix.reactnativenotifications.RNNotificationsPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.react.NavigationReactNativeHost;

import java.util.List;

import sg.com.srx.opensettings.OpenSettingsPackage;
import sg.com.srx.smslistener.SmsListenerPackage;

public class MainApplication extends NavigationApplication {

    //TODO: to remove after confirm no more crashing issue
//    private static CallbackManager mCallbackManager = CallbackManager.Factory.create();
//    protected static CallbackManager getCallbackManager() {
//        return mCallbackManager;
//    }

    private final ReactNativeHost mReactNativeHost =
            new NavigationReactNativeHost(this) {
                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }

                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Override
                public List<ReactPackage> getPackages() {
                    List<ReactPackage> packages = new PackageList(this).getPackages();
                    //Note : commented libraries are auto link
//                    packages.add(new VectorIconsPackage());
//                    packages.add(new RNDeviceInfo());
//                    packages.add(new FBSDKPackage());
                    packages.add(new OpenSettingsPackage());
//                    packages.add(new LottiePackage());
//                    packages.add(new ImagePickerPackage());
//                    packages.add(new ReactNativeWheelPickerPackage());
//                    packages.add(new SvgPackage());
//                    packages.add(new LinearGradientPackage());
//                    packages.add(BugsnagReactNative.getPackage());
//                    packages.add(new ReactNativeFirebaseAppPackage());
//                    packages.add(new ReactNativeFirebaseAnalyticsPackage());
//                    packages.add(new RNCWebViewPackage());
//                    packages.add(new RealmReactPackage());
                    packages.add(new RNNotificationsPackage(MainApplication.this));
//                    packages.add(new MapsPackage());
                    packages.add(new SmsListenerPackage());
//                    packages.add(new SplashScreenReactPackage());
//                    packages.add(new ReactNativeFirebaseMessagingPackage());
                    Log.e("PackageList", packages.size() + "Package list..");
                    return packages;
                }
            };

//    protected List<ReactPackage> getPackages() {
//        // Add additional packages you require here
//        // No need to add RnnPackage and MainReactPackage
//        return Arrays.<ReactPackage>asList(
//                new VectorIconsPackage(),
//                new RNDeviceInfo(),
//                new FBSDKPackage(),
//                new OpenSettingsPackage(),
//                new LottiePackage(),
//                new ImagePickerPackage(),
//                new ReactNativeWheelPickerPackage(),
//                new SvgPackage(),
//                new LinearGradientPackage(),
//                BugsnagReactNative.getPackage(),
//                new ReactNativeFirebaseAppPackage(),
//                new ReactNativeFirebaseAnalyticsPackage(),
//                new RNCWebViewPackage(),
//                //new RealmReactPackage(),
//                new RNNotificationsPackage(MainApplication.this),
//                new MapsPackage(),
//                new SmsListenerPackage(),
//                new SplashScreenReactPackage()
//        );
//    }

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

}
