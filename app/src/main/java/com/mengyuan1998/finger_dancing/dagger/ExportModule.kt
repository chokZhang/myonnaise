package com.mengyuan1998.finger_dancing.dagger

import com.mengyuan1998.finger_dancing.ui.export.ExportContract
import com.mengyuan1998.finger_dancing.ui.export.ExportPresenter
import dagger.Module
import dagger.Provides


@Module
class ExportModule {

    @Provides
    fun provideExportPresenter(
            exportView: ExportContract.View,
            deviceManager: DeviceManager
    ): ExportPresenter {
        return ExportPresenter(exportView, deviceManager)
    }

}
