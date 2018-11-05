package com.mengyuan1998.finger_dancing.dagger

import com.mengyuan1998.finger_dancing.ui.MainActivity
import com.mengyuan1998.finger_dancing.ui.export.ExportFragment
import com.mengyuan1998.finger_dancing.ui.scan.ScanDeviceFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ScanDeviceViewModule::class, ScanDeviceModule::class])
    abstract fun bindScanDeviceFragment(): ScanDeviceFragment

    @ContributesAndroidInjector(modules = [ExportViewModule::class, ExportModule::class])
    abstract fun bindExportFragment(): ExportFragment
}