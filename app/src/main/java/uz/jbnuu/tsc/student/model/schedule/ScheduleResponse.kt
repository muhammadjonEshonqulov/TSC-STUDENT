package uz.jbnuu.tsc.student.model.schedule

import uz.jbnuu.tsc.student.model.login.hemis.LoginHemisData

data class ScheduleResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<ScheduleData>?,
    val code: Int?,
)
