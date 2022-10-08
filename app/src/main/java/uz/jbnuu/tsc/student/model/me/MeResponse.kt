package uz.jbnuu.tsc.student.model.me

import uz.jbnuu.tsc.student.model.schedule.ScheduleData

data class MeResponse(
    val success: Boolean?,
    val error: String?,
    val data: MeData?,
    val code: Int?,
)
