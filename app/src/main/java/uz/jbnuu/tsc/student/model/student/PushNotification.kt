package uz.jbnuu.tsc.student.model.student

import uz.jbnuu.tsc.student.model.student.NotificationsData

data class PushNotification(
    val data: NotificationsData,
    val to: String
)