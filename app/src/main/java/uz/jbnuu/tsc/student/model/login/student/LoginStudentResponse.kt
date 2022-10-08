package uz.jbnuu.tsc.student.model.login.student

import uz.jbnuu.tsc.student.model.me.MeData

data class LoginStudentResponse(
    val status: Int?,
    val token: String?,
    val hemins_token: String?,
    val getme: MeData?
)
