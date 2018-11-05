package com.mengyuan1998.finger_dancing.dagger

import com.mengyuan1998.finger_dancing.MyoApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ContextModule::class,
    BuildersModule::class,
    MyonnaiseModule::class,
    DeviceManagerModule::class
])
interface ApplicationComponent : AndroidInjector<MyoApplication>