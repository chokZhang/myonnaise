package com.mengyuan1998.finger_dancing.ui.scan

import com.mengyuan1998.finger_dancing.BasePresenter
import com.mengyuan1998.finger_dancing.BaseView
import com.mengyuan1998.finger_dancing.ui.model.Device



interface ScanDeviceContract {

    interface View : com.mengyuan1998.finger_dancing.BaseView {

        fun showStartMessage()

        fun showEmptyListMessage()

        fun hideEmptyListMessage()

        fun wipeDeviceList()

        fun addDeviceToList(device: Device)

        fun populateDeviceList(list: List<Device>)

        fun showScanLoading()

        fun hideScanLoading()

        fun showScanError()

        fun showScanCompleted()

        fun navigateToControlDevice()
    }

    abstract class Presenter(override val view: com.mengyuan1998.finger_dancing.BaseView) : com.mengyuan1998.finger_dancing.BasePresenter<com.mengyuan1998.finger_dancing.BaseView>(view) {

        abstract fun onScanToggleClicked()

        abstract fun onDeviceSelected(index: Int)
    }
}