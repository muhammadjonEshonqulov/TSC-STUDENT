package uz.jbnuu.tsc.student.model.examTable

import uz.jbnuu.tsc.student.model.schedule.LessonPair
import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.schedule.TrainingType
import uz.jbnuu.tsc.student.model.semester.EducationYear

data class ExamTableData(
    val id: Int?,
    val subject: Subject?,
    val semester: TrainingType?,
    val educationYear: EducationYear?,
    val group: Subject?,
    val faculty: Subject?,
    val department: Subject?,
    val examType: TrainingType?,
    val finalExamType: TrainingType?,
    val employee: Subject?,
    val auditorium: TrainingType?,
    val lessonPair: LessonPair?,
    val examDate: Long?
)
