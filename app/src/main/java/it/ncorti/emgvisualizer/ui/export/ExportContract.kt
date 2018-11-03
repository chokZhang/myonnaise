package it.ncorti.emgvisualizer.ui.export

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView


interface ExportContract {

    interface View : BaseView {

        fun showConversationPressed()

        fun showConversationReset()

        fun showSpeechPressed()

        fun showSpeechReset()

        fun addSignText(content : String?)

        fun addVoiceText(content: String)

        fun addEditText(content: String)

        fun cleanEditText(content: String)

        fun saveCsvFile(content: String)

        fun showNotStreamingErrorMessage()
    }

    abstract class Presenter(override val view: BaseView) : BasePresenter<BaseView>(view) {

        abstract fun onConversationStart()

        abstract fun onConversationStop()

        abstract fun onSpeechStart()

        abstract fun onSpeechStop()

    }
}