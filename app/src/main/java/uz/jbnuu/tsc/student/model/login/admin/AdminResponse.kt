package uz.jbnuu.tsc.student.model.login.admin

data class AdminResponse(
    val status: Int?,
    val token: String?,
    val name: String?,
    val role_id: Int?
)
