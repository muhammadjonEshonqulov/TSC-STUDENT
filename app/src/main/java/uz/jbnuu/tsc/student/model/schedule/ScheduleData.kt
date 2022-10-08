package uz.jbnuu.tsc.student.model.schedule

data class ScheduleData(
    val subject: Subject?,
    val _week: Int?,
    val weekStartTime: Long?,
    val weekEndTime: Long?,
    val lesson_date: Long?,
    val additional: String?,
    val auditorium: Auditorium?,
    val trainingType: Auditorium?,
    val lessonPair: LessonPair?,
    val employee: Employee?,
)
