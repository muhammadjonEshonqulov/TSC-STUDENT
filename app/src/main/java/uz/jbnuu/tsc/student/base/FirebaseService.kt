 package uz.jbnuu.tsc.student.base

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.ui.MainActivity
import uz.jbnuu.tsc.student.utils.Prefs
import uz.jbnuu.tsc.student.utils.lg
import javax.inject.Inject
import kotlin.random.Random

const val CHANNEL_ID = "channel_tsc_jbnuu"

@AndroidEntryPoint
class FirebaseService : FirebaseMessagingService() {

    @Inject
    lateinit var prefs: Prefs

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(baseContext, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

//        val role = prefs.get(prefs.role, "")

        val bundle = bundleOf(
            "code" to message.data["code"]?.toInt(),
            "my_own_tittle" to message.data["my_own_tittle"],
            "my_own_text" to message.data["my_own_text"],
            "my_own_sub_text" to message.data["my_own_sub_text"],
        )

        val pendingIntent = NavDeepLinkBuilder(baseContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.graph_navigation)
            .setDestination(R.id.deadlineFragment)  // if (role == prefs.admin) R.id.allNotificationsFragment else R.id.allNotificationsFragment
            .setArguments(bundle)
            .createPendingIntent()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

//        val pendingIntent = PendingIntent.getActivities(this, 0, arrayOf(intent, intent), FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["my_own_tittle"])
            .setContentText(message.data["my_own_text"])
            .setSubText(message.data["my_own_sub_text"])
//            .setSettingsText(message.data["fromUserName"])
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        message.data["code"]?.toInt()?.let {
            lg("code in firebase "+it)
            prefs.save(prefs.error_code, it)
        }
        prefs.save(prefs.error, "Xatolik ->" + message.data["my_own_text"])
        notification.contentIntent = pendingIntent
        if (message.data["code"]?.toInt() == 100) {
            notificationManager.notify(notificationID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "TSC_JBNUU_channel"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "my channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        prefs.save(prefs.firebaseToken, newToken)
    }
}