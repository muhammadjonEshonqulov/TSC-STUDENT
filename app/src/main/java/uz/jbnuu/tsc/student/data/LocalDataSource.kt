package uz.jbnuu.tsc.student.data

import kotlinx.coroutines.flow.Flow
import uz.jbnuu.tsc.student.data.database.MyDao
import uz.jbnuu.tsc.student.model.send_location.SendLocationBody
import uz.jbnuu.tsc.student.model.subjects.Task
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: MyDao) {
    suspend fun insertSendLocationBody(data: SendLocationBody) {
        return dao.insertSendLocationBodyData(data)
    }

    fun getSendLocationBodyData(): Flow<List<SendLocationBody>> = dao.getSendLocationBodyData()

    suspend fun clearSendLocationBodyData() {
        return dao.clearSendLocationBodyData()
    }

    suspend fun insertTaskData(data: List<Task>) {
        return dao.insertTaskData(data)
    }

    fun getTaskData(): Flow<List<Task>> = dao.getTaskData()

    suspend fun clearTaskData() {
        return dao.clearTaskData()
    }
}
