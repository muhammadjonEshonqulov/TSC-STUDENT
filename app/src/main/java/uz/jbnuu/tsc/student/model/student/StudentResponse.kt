package uz.jbnuu.tsc.tutor.model.student

import uz.jbnuu.tsc.student.model.student.StudentData

data class StudentResponse(
    val status: Int?,
    val students: List<StudentData>?
)
