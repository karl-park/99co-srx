package sg.com.srx.smslistener;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Telephony;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;

public class SmsListenerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private ReactApplicationContext mContext;
    private SmsReceiver smsReceiver;
    private boolean isReceiverRegistered = true;

    public SmsListenerModule(ReactApplicationContext reactContext) {
        super(reactContext);

        //Add Life Cycle Event listener for react native

        this.mContext = reactContext;
        this.smsReceiver = new SmsReceiver();
        smsReceiver.setReactApplicationContext(reactContext);
        getReactApplicationContext().addLifecycleEventListener(this);
    }

    //Register and UnRegister BroadCast Receiver
    private void registerReceiver(BroadcastReceiver receiver) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getCurrentActivity() != null) {
            getCurrentActivity().registerReceiver(
                    receiver,
                    new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            );
            isReceiverRegistered = true;
            return;
        }

        if (getCurrentActivity() != null) {
            getCurrentActivity().registerReceiver(
                    receiver,
                    new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            );
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver(BroadcastReceiver receiver) {
        if (isReceiverRegistered && getCurrentActivity() != null) {
            getCurrentActivity().unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    public void onHostResume() {
        System.out.println("On Host Resume State");
        registerReceiver(smsReceiver);
    }

    @Override
    public void onHostPause() {
        System.out.println("On Host Pause State");
    }

    @Override
    public void onHostDestroy() {
        System.out.println("On Host Destory State");
        unregisterReceiver(smsReceiver);
    }

    @Override
    public String getName() {
        return "SmsListenerPackage";
    }
}
