package uz.jbnuu.tsc.student.model.semester


data class SemestersData(
    val id: Int?,
    val code: String?,
    val name: String?,
    val current: Boolean?,
    var currentExtra: Boolean? = null,
    val education_year: EducationYear?,
    val weeks: List<WeekData>?
)
