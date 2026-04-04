package com.example.hits_processes_2.common.app

import android.app.Application
import com.example.hits_processes_2.common.network.networkModule
import com.example.hits_processes_2.feature.authorization.authorizationModule
import com.example.hits_processes_2.feature.course_detail.courseDetailModule
import com.example.hits_processes_2.feature.courses.coursesModule
import com.example.hits_processes_2.feature.home.homeModule
import com.example.hits_processes_2.feature.task_creation.taskCreationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                authorizationModule,
                courseDetailModule,
                coursesModule,
                homeModule,
                taskCreationModule,
            )
        }
    }
}
