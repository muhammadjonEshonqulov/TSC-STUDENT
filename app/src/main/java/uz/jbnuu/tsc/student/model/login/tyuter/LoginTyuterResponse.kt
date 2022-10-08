package uz.jbnuu.tsc.student.model.login.tyuter

data class LoginTyuterResponse(
    val status: Int?,
    val token: String?,
    val ism: String?,
    val familya: String?,
    val role_id: Int?,
)
