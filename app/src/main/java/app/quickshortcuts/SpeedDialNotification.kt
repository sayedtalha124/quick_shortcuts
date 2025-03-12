package app.quickshortcuts

import ContactInfo
import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.CATEGORY_EVENT
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.provider.Settings
import androidx.core.net.toUri
import androidx.core.service.quicksettings.TileServiceCompat.startActivityAndCollapse
import app.quickshortcuts.SpeedDialNotification.Action.Button1
import app.quickshortcuts.SpeedDialNotification.Action.Button2
import app.quickshortcuts.SpeedDialNotification.Action.Button3
import app.quickshortcuts.SpeedDialNotification.Action.entries
import app.quickshortcuts.core.Utils.showLog

object SpeedDialNotification {


    private enum class Action(private val value: String) {
        Button1("app.quickshortcuts.action.Button1") {
            override fun title(context: Context) = "Button1"
        },
        Button2("app.quickshortcuts.action.Button2") {
            override fun title(context: Context) = "Button2"
        },
        Button3("app.quickshortcuts.action.Button3") {
            override fun title(context: Context) = "Button3"
        },
        ;

        fun intent(phone: String): Intent {
            showLog("intent called")
            return Intent(Intent.ACTION_CALL).setData("tel:$phone".toUri())
        }

        fun pendingIntent(context: Context, phone: String): PendingIntent? {
            val intent = intent(phone)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT //Recommended for API 31+
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            return PendingIntent.getActivity(
                context,
                0,
                intent,
                flags
            ) //Use getActivity for ACTION_CALL
        }

        fun action(context: Context, phone: String, name: String): Notification.Action.Builder {
            showLog("action called")
            return Notification.Action.Builder(
                Icon.createWithResource(context, 0),
                name,
                pendingIntent(context, phone)
            )
        }

        companion object {
            fun parse(value: String?): Action? = entries.firstOrNull { it.value == value }
        }


        abstract fun title(context: Context): CharSequence?
    }

    fun Context.notificationManager(): NotificationManager? =
        getSystemService(NotificationManager::class.java)

    fun Context.find() =
        notificationManager()?.activeNotifications?.firstOrNull { it.id == R.id.notification_id }?.notification

    fun Context.handle(intent: Intent?, phone: List<ContactInfo>) =
        when (Action.parse(intent?.action)) {
            Action.Button1 -> update(phone = phone)
            Action.Button2 -> update(phone = phone)
            Action.Button3 -> update(phone = phone)
            null -> Unit
        }

    fun Context.toggle(phone: List<ContactInfo>) =
        if (find() == null) show(phone = phone) else cancel()

    private fun Context.cancel() = notificationManager()?.cancel(R.id.notification_id) ?: Unit

    private fun Context.update(phone: List<ContactInfo>) {
        if (find() == null) {
            show(phone = phone)


        }
    }

    private fun Context.show(phone: List<ContactInfo>) {
        var phone1: String? = phone.getOrNull(0)?.dial
        var phone2: String? = phone.getOrNull(1)?.dial
        var phone3: String? = phone.getOrNull(2)?.dial
        var name1: String? = phone.getOrNull(0)?.displayName
        var name2: String? = phone.getOrNull(1)?.displayName
        var name3: String? = phone.getOrNull(2)?.displayName

        val notification = Notification.Builder(this, getString(R.string.notification_channel_id))
            .setCategory(CATEGORY_EVENT)
            .setVisibility(VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.phone)
            .setSubText("Speed Dial")
            .setContentText("Tap contact to dial")
            .addAction(Button1.action(this, phone1.toString(), name1.toString().trimEnd()).build())
            .addAction(Button2.action(this, phone2.toString(), name2.toString().trimEnd()).build())
            .addAction(Button3.action(this, phone3.toString(), name3.toString().trimEnd()).build())
            .build()
        createNotificationChannel()
        notificationManager()?.notify(R.id.notification_id, notification)
    }

    private fun Context.createNotificationChannel() {
        val id = getString(R.string.notification_channel_id)
        val name: CharSequence = getString(R.string.app_name)
        val channel = NotificationChannel(id, name, IMPORTANCE_HIGH).apply {
            setBypassDnd(true)
            lockscreenVisibility = VISIBILITY_PUBLIC
        }
        notificationManager()?.createNotificationChannel(channel)
    }

    fun Context.requestNotificationsPermission() =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }.let {
           startActivity(it)
        }
}