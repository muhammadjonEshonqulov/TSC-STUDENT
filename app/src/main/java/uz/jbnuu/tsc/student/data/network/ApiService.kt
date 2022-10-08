package uz.jbnuu.tsc.student.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import uz.jbnuu.tsc.student.model.SubjectResponse
import uz.jbnuu.tsc.student.model.attendance.AttendanceResponse
import uz.jbnuu.tsc.student.model.examTable.ExamTableResponse
import uz.jbnuu.tsc.student.model.login.LogoutResponse
import uz.jbnuu.tsc.student.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.login.student.LoginStudentResponse
import uz.jbnuu.tsc.student.model.me.MeResponse
import uz.jbnuu.tsc.student.model.performance.PerformanceResponse
import uz.jbnuu.tsc.student.model.reference.ReferenceResponse
import uz.jbnuu.tsc.student.model.schedule.ScheduleResponse
import uz.jbnuu.tsc.student.model.semester.SemestersResponse
import uz.jbnuu.tsc.student.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.student.model.send_location.SendLocationBody
import uz.jbnuu.tsc.student.model.send_location.SendLocationResponse
import uz.jbnuu.tsc.student.model.student.PushNotification
import uz.jbnuu.tsc.student.model.subjects.SubjectsResponse
import uz.jbnuu.tsc.student.utils.Constants.Companion.CONTENT_TYPE
import uz.jbnuu.tsc.student.utils.Constants.Companion.SERVER_KEY

interface ApiService {

    @POST("login_student")
    suspend fun loginStudent(@Body loginStudentBody: LoginStudentBody): Response<LoginStudentResponse>

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST
    suspend fun postNotification(@Url full_url: String, @Body notification: PushNotification): Response<ResponseBody>

    @POST("auth/login")
    suspend fun loginHemis(@Body loginHemisBody: LoginStudentBody): Response<LoginHemisResponse>

    @GET("account/me")
    suspend fun me(): Response<MeResponse>

    @GET("student/reference")
    suspend fun studentReference(): Response<ReferenceResponse>

    @Streaming
    @GET
    suspend fun studentReferenceDownload(@Url url: String): Response<ResponseBody>

    @GET("education/schedule")
    suspend fun schedule(@Query("week") week: Int): Response<ScheduleResponse>

    @GET("education/subjects")
    suspend fun subjects(): Response<SubjectsResponse>

    @GET("education/subject")
    suspend fun subject(@Query("subject") subject: Int?, @Query("semester") semester: String): Response<SubjectResponse>

    @GET("education/semesters")
    suspend fun semesters(): Response<SemestersResponse>

    @GET("education/performance")
    suspend fun performance(): Response<PerformanceResponse>

    @GET("education/attendance")
    suspend fun attendance(@Query("semester") semester: String?): Response<AttendanceResponse>

    @GET("logout")
    suspend fun logout(): Response<LogoutResponse>

    @POST("send_location")
    suspend fun sendLocation(@Body sendLocationBody: SendLocationBody): Response<SendLocationResponse>

    @POST("send_location_array")
    suspend fun sendLocationArray(@Body sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse>

    @GET("education/exam-table")
    suspend fun examTable(@Query("semester") semester: String?): Response<ExamTableResponse>

//    @POST("orders")
//    suspend fun orders(@Query("table_id") table_id: Int, @Query("waiter_id") waiter_id: Int, @Query("lang") lang: Int, @Body orders: OrderBody): Response<OrderResponse>
}