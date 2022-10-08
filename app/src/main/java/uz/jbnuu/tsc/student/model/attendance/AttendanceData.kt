package uz.jbnuu.tsc.student.model.attendance

import uz.jbnuu.tsc.student.model.schedule.Employee
import uz.jbnuu.tsc.student.model.schedule.LessonPair
import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.schedule.TrainingType
import uz.jbnuu.tsc.student.model.semester.SemestersData

data class AttendanceData(
    val subject: Subject?,
    val semester: SemestersData?,
    val trainingType: TrainingType?,
    val lessonPair: LessonPair?,
    val employee: Employee?,
    val absent_on: Int?,
    val absent_off: Int?,
    val lesson_date: Long?
)
