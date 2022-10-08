package uz.jbnuu.tsc.student.ui.deadlines

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.model.subjects.Task
import uz.jbnuu.tsc.student.ui.MainActivity
import uz.jbnuu.tsc.student.utils.Prefs
import uz.jbnuu.tsc.student.utils.lg
import uz.jbnuu.tsc.student.utils.toGo
import java.text.SimpleDateFormat
import java.util.*


class DeadlineNotifyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID_TSC = "tsc_jbnuu_my_channel"
        const val NOTIFICATION_ID = 1
    }

    private fun showNotification(task: Task, message: String) {

        val bundle = bundleOf("task_id" toGo task.id)

//        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//        notificationIntent.action = Intent.ACTION_MAIN
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

//        val snoozeIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
//            action = ACTION_SNOOZE
//            putExtra(EXTRA_NOTIFICATION_ID, 0)
//        }

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.graph_navigation)
            .setArguments(bundle)
            .setDestination(R.id.deadlineFragment)
            .createPendingIntent()


        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_TSC)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle("" + task.subject?.name?.get(0)?.uppercase() + task.subject?.name?.substring(1)?.lowercase())
            .setContentText("Topshirish oxirgi vaqti " + getDateTime(task.deadline) + "\n" + message)
            .setSubText("" + task.name)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "My Own TSC Channel"
            val channelDescription = "My Own TSC Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID_TSC, channelName, channelImportance).apply {
                description = channelDescription
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)

            with(NotificationManagerCompat.from(applicationContext)) {
                notify(task.id, notification.build())
            }

        }
    }


    private fun getDateTime(s: Long?): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            s?.let {
                val netDate = Date(it * 1000)
                sdf.format(netDate)
            }
        } catch (e: Exception) {
            e.toString()
        }
    }


    override fun doWork(): Result {
        lg("Worker is working ")
        val prefs = Prefs(applicationContext)
        val deadlines = Gson().fromJson(prefs.get("Deadlines", ""), Array<Task>::class.java).toList()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentDate = sdf.format(Date())
        lg("currentDate-> $currentDate")

        deadlines.forEach {
            val currentDay = currentDate.split(" ").first().split("-").last().toInt()
            val currentMonth = currentDate.split(" ").first().split("-")[1]
            val currentYear = currentDate.split(" ").first().split("-").first()
            val currentHour = currentDate.split(" ").last().split(":").first().toInt()
            val currentMinute = currentDate.split(" ").last().split(":").last().toInt()

            val taskDay = getDateTime(it.deadline)?.split(" ")?.first()?.split("-")?.last()?.toInt()
            val taskMonth = getDateTime(it.deadline)?.split(" ")?.first()?.split("-")?.get(1)
            val taskYear = getDateTime(it.deadline)?.split(" ")?.first()?.split("-")?.first()
            val taskHour = getDateTime(it.deadline)?.split(" ")?.last()?.split(":")?.first()?.toInt()
            val taskMinute = getDateTime(it.deadline)?.split(" ")?.last()?.split(":")?.last()?.toInt()

            if (currentYear == taskYear && currentMonth == taskMonth) {
                if ((taskDay?.minus(currentDay) ?: 0) == 2) {
                    showNotification(it, "2 kun vaqtingiz qoldi!")

                } else if ((taskDay?.minus(currentDay) ?: 0) == 1) {
                    showNotification(it, "1 kun vaqtingiz qoldi. Bajaring!")

                } else if ((taskDay?.minus(currentDay) ?: 0) == 0) {
                    if ((taskHour?.minus(currentHour) ?: 0) in 7..23) {
                        showNotification(it, "${taskHour?.minus(currentHour)} soat vaqtingiz qoldi. Shoshiling!")
                    } else if ((taskHour?.minus(currentHour) ?: 0) == 6) {
                        showNotification(it, "6 soat vaqtingiz qoldi. Shoshiling!")

                    } else if ((taskHour?.minus(currentHour) ?: 0) == 3) {
                        showNotification(it, "3 soat vaqtingiz qoldi. Gazini bosing!")

                    } else if ((taskHour?.minus(currentHour) ?: 0) == 2) {
                        showNotification(it, "2 soat vaqtingiz qoldi. Gazini bosing!")

                    } else if ((taskHour?.minus(currentHour) ?: 0) == 1) {
                        showNotification(it, "1 soat vaqtingiz qoldi. Gazini bosing!")

                    } else if ((taskMinute?.minus(currentMinute) ?: 0) in 10..50) {
                        showNotification(it, "${taskMinute?.minus(currentMinute)} minut vaqtingiz qoldi. Gazini bosing!")
                    }
                }
//                else if ((taskMinute?.minus(currentMinute) ?: 0) in 10..19) {
//                    showNotification(it)
//                }
            }

        }

        return Result.success()
    }
}