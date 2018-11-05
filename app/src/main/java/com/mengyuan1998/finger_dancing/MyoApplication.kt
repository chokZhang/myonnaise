package com.mengyuan1998.finger_dancing

import android.app.Activity
import android.app.Application
import com.mengyuan1998.finger_dancing.dagger.ApplicationComponent
import com.mengyuan1998.finger_dancing.dagger.ContextModule
import com.mengyuan1998.finger_dancing.dagger.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject



class MyoApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>


    companion object {
        @JvmStatic lateinit var applicationComponent : ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
                .builder()
                .contextModule(ContextModule(applicationContext))
                .build()

        applicationComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}

