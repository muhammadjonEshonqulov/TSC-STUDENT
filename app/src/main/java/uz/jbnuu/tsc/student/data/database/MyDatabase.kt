package uz.jbnuu.tsc.student.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.send_location.SendLocationBody
import uz.jbnuu.tsc.student.model.subjects.Task
import uz.jbnuu.tsc.student.model.subjects.converters.*


@Database(
    entities = [
        SendLocationBody::class, Task::class
    ], version = 1
)
@TypeConverters(
    SubjectConverter::class,
    TrainingTypeConverter::class,
    FilterDataConverter::class,
    StudentTaskActivityConverter::class,
    FilesDataConverter::class,
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun dao(): MyDao

    companion object {

        private var instance: MyDatabase? = null

//        fun initDatabase(context: Context) {
//            synchronized(this) {
//                if (instance == null) {
//                    instance = Room
//                        .databaseBuilder(context.applicationContext, MyDatabase::class.java, "tsc_table.db")
//                        .fallbackToDestructiveMigration()
//                        .build()
//                }
//            }
//        }
//
//        fun getDatabase() = instance
    }
}