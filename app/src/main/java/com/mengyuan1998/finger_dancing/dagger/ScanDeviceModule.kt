package com.mengyuan1998.finger_dancing.dagger

import com.mengyuan1998.finger_dancing.ui.scan.ScanDeviceContract
import com.mengyuan1998.finger_dancing.ui.scan.ScanDevicePresenter
import com.ncorti.myonnaise.Myonnaise
import dagger.Module
import dagger.Provides

@Module
class ScanDeviceModule {

    @Provides
    fun provideScanDevicePresenter(
            scanDeviceView: ScanDeviceContract.View,
            myonnaise: Myonnaise,
            deviceManager: DeviceManager
    ): ScanDevicePresenter {
        return ScanDevicePresenter(scanDeviceView, myonnaise, deviceManager)
    }

}
