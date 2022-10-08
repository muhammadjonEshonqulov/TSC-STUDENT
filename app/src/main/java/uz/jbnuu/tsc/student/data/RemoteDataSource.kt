package uz.jbnuu.tsc.student.data

import okhttp3.ResponseBody
import retrofit2.Response
import uz.jbnuu.tsc.student.data.network.ApiService
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
import javax.inject.Inject
import javax.inject.Named

class RemoteDataSource @Inject constructor(@Named("provideApiService") val apiService: ApiService, @Named("provideApiServiceHemis") val apiServiceHemis: ApiService) {

    suspend fun loginStudent(loginStudentBody: LoginStudentBody): Response<LoginStudentResponse> {
        return apiService.loginStudent(loginStudentBody)
    }

    suspend fun postNotification(full_url: String, notification: PushNotification): Response<ResponseBody> {
        return apiService.postNotification(full_url, notification)
    }

    suspend fun me(): Response<MeResponse> {
        return apiServiceHemis.me()
    }

    suspend fun studentReference(): Response<ReferenceResponse> {
        return apiServiceHemis.studentReference()
    }

    suspend fun studentReferenceDownload(url: String, token: String): Response<ResponseBody> {
        return apiServiceHemis.studentReferenceDownload(url)
    }

    suspend fun loginHemis(loginHemisBody: LoginStudentBody): Response<LoginHemisResponse> {
        return apiServiceHemis.loginHemis(loginHemisBody)
    }

    suspend fun subjects(): Response<SubjectsResponse> {
        return apiServiceHemis.subjects()
    }

    suspend fun subject(subject: Int?, semester: String): Response<SubjectResponse> {
        return apiServiceHemis.subject(subject, semester)
    }

    suspend fun semesters(): Response<SemestersResponse> {
        return apiServiceHemis.semesters()
    }

    suspend fun schedule(week: Int): Response<ScheduleResponse> {
        return apiServiceHemis.schedule(week)
    }

    suspend fun performance(): Response<PerformanceResponse> {
        return apiServiceHemis.performance()
    }

    suspend fun attendance(semester: String?): Response<AttendanceResponse> {
        return apiServiceHemis.attendance(semester)
    }

    suspend fun logout(): Response<LogoutResponse> {
        return apiService.logout()
    }

    suspend fun sendLocation(sendLocationBody: SendLocationBody): Response<SendLocationResponse> {
        return apiService.sendLocation(sendLocationBody)
    }


    suspend fun sendLocationArray(sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse> {
        return apiService.sendLocationArray(sendLocationArrayBody)
    }


    suspend fun examTable(semester: String?): Response<ExamTableResponse> {
        return apiServiceHemis.examTable(semester)
    }

}