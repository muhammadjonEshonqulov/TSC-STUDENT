package uz.jbnuu.tsc.student.model.subjects


data class SubjectsResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<SubjectsData>?,
    val code: Int?,
)
