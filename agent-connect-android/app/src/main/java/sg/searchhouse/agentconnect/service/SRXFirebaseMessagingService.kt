package sg.searchhouse.agentconnect.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.app.NotificationParams
import sg.searchhouse.agentconnect.event.chat.ChatNewNotificationEvent
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.view.activity.main.SplashActivity
import sg.searchhouse.agentconnect.view.activity.auth.SignInActivity
import sg.searchhouse.agentconnect.view.activity.listing.portal.PortalListingsActivity
import sg.searchhouse.agentconnect.view.activity.main.MainActivity
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import kotlin.random.Random

//TODO: in future after 1.6, combine background and foreground notification in one file
class SRXFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i("MessagingService", p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleRemoteMessage(remoteMessage)
    }

    private fun handleRemoteMessage(remoteMessage: RemoteMessage) {
        val entries: Map<String, String> = remoteMessage.data
        if (entries.isNotEmpty()) {
            val notification = NotificationParams(params = entries)
            val type = notification.getNotificationType()
            val message = remoteMessage.notification?.body ?: ""
            if (type == AppConstant.NOTIFICATION_TYPE_SSM) {
                val conversationId = notification.getConversationId()
                val userName = remoteMessage.notification?.title ?: ""
                val messageId = notification.getMessageId()
                val enquiryId = notification.getEnquiryId()
                val announcement = notification.getAnnouncement()

                when {
                    !TextUtils.isEmpty(announcement) && StringUtil.equals(
                        announcement,
                        AppConstant.NOTIFICATION_TYPE_ANNOUNCEMENT_LISTING,
                        ignoreCase = true
                    ) -> {
                        showPortalListingsScreen(message)
                    }
                    !TextUtils.isEmpty(conversationId.trim()) -> {
                        showMessagingScreen(messageId, conversationId, enquiryId, userName, message)
                    }
                    !TextUtils.isEmpty(enquiryId.trim()) -> {
                        showEnquiryPopup(messageId, conversationId, enquiryId, message)
                    }
                    else -> openApp(message)
                }
            } else {
                //note: for normal test notification
                openApp(message)
            }
        }
    }

    private fun showPortalListingsScreen(message: String) {
        val currentUser = SessionUtil.getCurrentUser()
        val intent = Intent()
        if (currentUser != null) {
            intent.setClass(this, PortalListingsActivity::class.java)
        } else {
            intent.setClass(this, SignInActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        sendNotification(
            this.resources.getString(R.string.label_imported_listing),
            message,
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

    }

    private fun showMessagingScreen(
        messageId: String,
        conversationId: String,
        enquiryId: String,
        userName: String,
        message: String
    ) {
        val currentUser = SessionUtil.getCurrentUser()
        val intent = Intent()
        if (currentUser != null) {
            RxBus.publish(ChatNewNotificationEvent(messageId, conversationId, enquiryId))
            intent.setClass(this, ChatMessagingActivity::class.java)
            intent.putExtra(ChatMessagingActivity.EXTRA_KEY_CONVERSATION_ID, conversationId)
        } else {
            intent.setClass(this, SignInActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        sendNotification(
            userName,
            message,
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    private fun showEnquiryPopup(
        messageId: String,
        conversationId: String,
        enquiryId: String,
        message: String
    ) {
        val currentUser = SessionUtil.getCurrentUser()
        val intent = Intent()
        if (currentUser != null) {
            RxBus.publish(ChatNewNotificationEvent(messageId, conversationId, enquiryId))
            intent.setClass(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_KEY_ENQUIRY_ID, enquiryId)
        } else {
            intent.setClass(this, SignInActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        sendNotification(
            this.resources.getString(R.string.label_customer_enquiry),
            message,
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    private fun openApp(message: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        sendNotification(
            this.resources.getString(R.string.app_name),
            message,
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    //TODO: in future move to notification util
    private fun sendNotification(
        contentTitle: String,
        contentText: String,
        contentImage: Bitmap? = null,
        pendingIntent: PendingIntent
    ) {
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel("ID", "Name", NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.enableVibration(true)
                manager.createNotificationChannel(channel)
                NotificationCompat.Builder(this, channel.id)
            } else {
                NotificationCompat.Builder(this)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
            }

        //TODO use notificationTarget to show image
        contentImage?.let { bitmap -> builder.setLargeIcon(bitmap) }

        val notification = builder
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000))
            .setSmallIcon(R.drawable.ic_notification)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setDefaults(NotificationCompat.DEFAULT_ALL).build()

        manager.notify(
            this.getString(R.string.app_name),
            generateUniqueNotificationId(),
            notification
        )
    }

    private fun generateUniqueNotificationId(): Int {
        return Random.nextInt()
    }
}