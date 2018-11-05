package com.mengyuan1998.finger_dancing.dagger

import com.mengyuan1998.finger_dancing.ui.export.ExportContract
import com.mengyuan1998.finger_dancing.ui.export.ExportFragment
import dagger.Binds
import dagger.Module



@Module
abstract class ExportViewModule {

    @Binds
    abstract fun provideExportView(exportFragment: ExportFragment): ExportContract.View
}