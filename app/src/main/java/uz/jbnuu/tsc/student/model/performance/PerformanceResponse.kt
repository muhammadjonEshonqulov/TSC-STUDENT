package uz.jbnuu.tsc.student.model.performance


data class PerformanceResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<PerformanceData>?,
    val code: Int?,
)
