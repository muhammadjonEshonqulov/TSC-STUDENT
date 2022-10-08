package uz.jbnuu.tsc.student.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.jbnuu.tsc.student.app.App
import uz.jbnuu.tsc.student.data.database.MyDatabase
import uz.jbnuu.tsc.student.utils.Constants.Companion.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase() = Room.databaseBuilder(App.context, MyDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDatabaseDao(myDao: MyDatabase) = myDao.dao()
}