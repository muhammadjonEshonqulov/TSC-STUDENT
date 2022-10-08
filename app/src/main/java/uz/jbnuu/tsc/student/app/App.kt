package uz.jbnuu.tsc.student.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        PRDownloader.initialize(applicationContext);

//        PRDownloader.initialize(this, PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).setReadTimeout(3000).setConnectTimeout(3000).build())
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}