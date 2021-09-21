package sg.com.srx.smslistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class SmsReceiver extends BroadcastReceiver {

    private static String SRX_SMS_SENDER = "SRXPROPERTY";
    private static String SmsEventName = "SmsListener";
    private ReactApplicationContext mContext = null;

    public SmsReceiver() {
        super();
    }

    public void setReactApplicationContext(ReactApplicationContext context) {
        this.mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    //Get SMS from protocol data unit to SmsMessage object
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                    //Get Sender Number
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = "";

                    if (!senderNum.equalsIgnoreCase("") && senderNum.toUpperCase().contains(SRX_SMS_SENDER)) {

                        message = currentMessage.getDisplayMessageBody();

                        //Create SmsProps Object with sender number and message
                        SmsProps smsProps = new SmsProps(senderNum, message);

                        Log.i("SmsReceiver ::: ", "Sender Number : " +
                                senderNum + "; Incoming Messages: " + message);

                        if (smsProps != null) {
                            handleSendText(smsProps);
                        }

                    }


                }//end of for loop
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    private void handleSendText(SmsProps smsProps) {
        if (mContext != null) {

            if (smsProps != null) {
                //Convert to writable native map to send to react native codes
                WritableNativeMap writableNativeMap = new WritableNativeMap();
                writableNativeMap.putString("address", smsProps.getAddress());
                writableNativeMap.putString("message", smsProps.getMessage());

                mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(SmsEventName, writableNativeMap);
            }
        } else {
            System.out.println("M Context is NULL");
            return;
        }
    }
}
