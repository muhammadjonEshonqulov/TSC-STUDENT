package uz.jbnuu.tsc.student.model.reference

data class ReferenceResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<ReferenceData>?,
    val code: Int?,
)
