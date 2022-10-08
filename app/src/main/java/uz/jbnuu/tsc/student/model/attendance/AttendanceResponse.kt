package uz.jbnuu.tsc.student.model.attendance


data class AttendanceResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<AttendanceData>?,
    val code: Int?,
)
