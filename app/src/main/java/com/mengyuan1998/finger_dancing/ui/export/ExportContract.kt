package com.mengyuan1998.finger_dancing.ui.export

import com.mengyuan1998.finger_dancing.BasePresenter
import com.mengyuan1998.finger_dancing.BaseView


interface ExportContract {

    interface View : com.mengyuan1998.finger_dancing.BaseView {

        fun showConversationPressed()

        fun showConversationReset()

        fun showSpeechPressed()

        fun showSpeechReset()

        fun disableStartCollectingButton()

      fun addSignText(content : String?)

        fun addVoiceText(content: String)

        fun addEditText(content: String)

        fun cleanEditText(content: String)

        fun saveCsvFile(content: String)

        fun showNotStreamingErrorMessage()

        fun showImg()

        fun disImg()
    }

    abstract class Presenter(override val view: com.mengyuan1998.finger_dancing.BaseView) : com.mengyuan1998.finger_dancing.BasePresenter<com.mengyuan1998.finger_dancing.BaseView>(view) {

        abstract fun onConversationStart()

        abstract fun onConversationStop()

        abstract fun onSpeechStart()

        abstract fun onSpeechStop()

    }

    interface ReturnListener{
        fun getReturn()
    }
}