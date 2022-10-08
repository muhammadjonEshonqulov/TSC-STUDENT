package uz.jbnuu.tsc.student.model.semester

data class SemestersResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<SemestersData>?,
    val code: Int?,
)
