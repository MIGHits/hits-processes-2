package com.example.hits_processes_2.common.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.network.networkModule
import com.example.hits_processes_2.common.resources.resourceModule
import com.example.hits_processes_2.feature.authorization.authorizationModule
import com.example.hits_processes_2.feature.course_detail.courseDetailModule
import com.example.hits_processes_2.feature.courses.coursesModule
import com.example.hits_processes_2.feature.file_attachment.fileAttachmentModule
import com.example.hits_processes_2.feature.file_attachment.service.FileTransferService
import com.example.hits_processes_2.feature.home.homeModule
import com.example.hits_processes_2.feature.task_creation.taskCreationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                resourceModule,
                authorizationModule,
                courseDetailModule,
                coursesModule,
                fileAttachmentModule,
                homeModule,
                taskCreationModule,
            )
        }
    }

    private fun createNotificationChannels() {
        val notificationChannel = NotificationChannel(
            FileTransferService.CHANNEL_ID,
            getString(R.string.file_attachment_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.file_attachment_channel_description)
        }

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(notificationChannel)
    }
}
