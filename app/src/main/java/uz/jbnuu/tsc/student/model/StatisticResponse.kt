package uz.jbnuu.tsc.student.model

data class StatisticResponse(
    val status: Int?,
    val groups_count: Int?,
    val students_count: Int?,
    val student_male: Int?,
    val student_female: Int?,
    val student_grant: Int?,
    val student_contract: Int?,
    val student_nogiron: Int?,
    val student_yetim: Int?
)
