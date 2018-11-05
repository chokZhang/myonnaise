package com.mengyuan1998.finger_dancing.dagger

import com.mengyuan1998.finger_dancing.ui.scan.ScanDeviceContract
import com.mengyuan1998.finger_dancing.ui.scan.ScanDeviceFragment
import dagger.Binds
import dagger.Module

@Module
abstract class ScanDeviceViewModule {

    @Binds
    abstract fun provideScanDeviceView(scanFragment: ScanDeviceFragment): ScanDeviceContract.View
}