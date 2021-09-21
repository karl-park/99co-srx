package sg.searchhouse.agentconnect.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.getNotificationManager
import sg.searchhouse.agentconnect.enumeration.app.SrxNotificationChannel
import kotlin.random.Random

object NotificationUtil {
    private fun createNotificationChannel(
        context: Context,
        notificationChannel: SrxNotificationChannel
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.run {
                val name = getString(notificationChannel.channelName)
                val descriptionText = getString(notificationChannel.description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel =
                    NotificationChannel(notificationChannel.channelId, name, importance).apply {
                        description = descriptionText
                    }
                // Register the channel with the system
                getNotificationManager().createNotificationChannel(channel)
            }
        }
    }

    fun showNotification(
        context: Context,
        @StringRes title: Int?,
        @StringRes content: Int?,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        pendingIntent: PendingIntent? = null,
        actions: List<NotificationCompat.Action>? = null
    ): Int {
        return showNotification(
            context,
            title?.run { context.getString(this) },
            content?.run { context.getString(this) },
            notificationChannel,
            priority,
            pendingIntent,
            actions
        )
    }

    fun showNotification(
        context: Context,
        title: String?,
        content: String?,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        pendingIntent: PendingIntent? = null,
        actions: List<NotificationCompat.Action>? = null
    ): Int {
        // No penalty to call create channel multiple times
        // rf. https://developer.android.com/training/notify-user/build-notification
        createNotificationChannel(context, notificationChannel)

        val builder = NotificationCompat.Builder(context, notificationChannel.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(priority)

        title?.run { builder.setContentTitle(this) }
        content?.run { builder.setContentText(this) }

        pendingIntent?.run { builder.setContentIntent(this) }
        actions?.map { builder.addAction(it) }

        val notification = builder.build()

        return notify(context, notification)
    }

    fun showCustomNotification(
        context: Context,
        customContentView: RemoteViews,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        pendingIntent: PendingIntent? = null
    ): Int {
        // No penalty to call create channel multiple times
        // rf. https://developer.android.com/training/notify-user/build-notification
        createNotificationChannel(context, notificationChannel)

        val builder = NotificationCompat.Builder(context, notificationChannel.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(priority)
            .setCustomContentView(customContentView)

        pendingIntent?.run { builder.setContentIntent(this) }

        val notification = builder.build()
        return notify(context, notification)
    }

    fun showIndeterminateProgressNotification(
        context: Context,
        @StringRes title: Int?,
        @StringRes content: Int?,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ): Int {
        return showIndeterminateProgressNotification(
            context,
            title?.run { context.getString(this) },
            content?.run { context.getString(this) },
            notificationChannel,
            priority
        )
    }

    fun showIndeterminateProgressNotification(
        context: Context,
        title: String?,
        content: String?,
        notificationChannel: SrxNotificationChannel = SrxNotificationChannel.GENERAL,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ): Int {
        // No penalty to call create channel multiple times
        // rf. https://developer.android.com/training/notify-user/build-notification
        createNotificationChannel(context, notificationChannel)

        val builder = NotificationCompat.Builder(context, notificationChannel.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setProgress(100, 0, true)
            .setPriority(priority)

        title?.run { builder.setContentTitle(this) }
        content?.run { builder.setContentText(this) }

        val notification = builder.build()
        return notify(context, notification)
    }

    private fun notify(context: Context, notification: Notification): Int {
        val notificationId = Random.nextInt()
        context.run {
            getNotificationManager().notify(
                NOTIFICATION_TAG,
                notificationId,
                notification
            )
        }
        return notificationId
    }

    fun dismiss(context: Context, notificationId: Int) {
        context.run {
            getNotificationManager().cancel(
                NOTIFICATION_TAG,
                notificationId
            )
        }
    }

    //TODO: to refine in future if there is better way .
    fun createTaskStackBuilder(
        context: Context,
        parentIntent: Intent,
        nextIntent: Intent
    ): TaskStackBuilder {
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntentWithParentStack(parentIntent)
        taskStackBuilder.addNextIntent(nextIntent)
        return taskStackBuilder
    }

    // TODO Maybe customize if needed
    private const val NOTIFICATION_TAG = "SRX_NOTIFICATION"
}