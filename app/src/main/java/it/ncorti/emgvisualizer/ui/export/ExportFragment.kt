package it.ncorti.emgvisualizer.ui.export

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import it.ncorti.emgvisualizer.BaseFragment
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.Utilities.MessageManager
import it.ncorti.emgvisualizer.Utilities.VoiceMessage
import it.ncorti.emgvisualizer.adpter.ConversationMessagesRVAdapter
import kotlinx.android.synthetic.main.layout_export.*
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


private const val REQUEST_WRITE_EXTERNAL_CODE = 2

class ExportFragment : BaseFragment<ExportContract.Presenter>(), ExportContract.View {

    companion object {
        fun newInstance() = ExportFragment()
    }

    @Inject
    lateinit var exportPresenter: ExportPresenter

    private var fileContentToSave: String? = null

    private var isClicked : Boolean = false

    private val adapter : ConversationMessagesRVAdapter = ConversationMessagesRVAdapter(activity)

    private var thread :Thread? = null
    private var handler :Handler =object : Handler(){     //此处的object 要加，否则无法重写 handlerMessage
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if(msg?.what == 0){

            }

        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        attachPresenter(exportPresenter)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("createIt", "index")
        val root = inflater.inflate(R.layout.layout_export, container, false)

        setHasOptionsMenu(true)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("createIt", "init")
        startCommunicate_img.setOnClickListener{
            if(!isClicked){
                Log.d("createIt", "click")
                exportPresenter.onCollectionTogglePressed()
            }
            else {
                Log.d("createIt", "clicked")
                exportPresenter.onSavePressed()
            }
            isClicked = !isClicked
        }

        conversation_display_rv.adapter = adapter
        conversation_display_rv.layoutManager = LinearLayoutManager(activity)

        send_conversation.setOnClickListener {
            var content = edit_conversation.text.toString();
            if(content.length > 0){
                MessageManager.getInstance().buildTextMessage(content)
                edit_conversation.text.clear()
            }
        }

        MessageManager.getInstance().addNewNoticeTarget(object : MessageManager.NoticeMessageChanged {

            override fun onNewMessageAdd() {
                adapter.updateMessageList()
                adapter.notifyDataSetChanged()
                conversation_display_rv.scrollToPosition(adapter.getItemCount() - 1)
            }

            override fun onMessageContentChange() {
                adapter.updateMessageList()
                adapter.notifyDataSetChanged()
            }

            override fun onSignCaptureEnd() {
                adapter.notifyDataSetChanged()
            }

            override fun onSignCaptureStart() {
                adapter.notifyDataSetChanged()
            }

        })



        /*button_start_collecting.setOnClickListener { exportPresenter.onCollectionTogglePressed() }
        button_reset_collecting.setOnClickListener { exportPresenter.onResetPressed() }
        button_share.setOnClickListener { exportPresenter.onSharePressed() }
        button_save.setOnClickListener { exportPresenter.onSavePressed() }*/
    }

    override fun enableStartCollectingButton() {
        //button_start_collecting.isEnabled = true
    }

    override fun disableStartCollectingButton() {
        //button_start_collecting.isEnabled = false
    }

    override fun showNotStreamingErrorMessage() {
        Toast.makeText(activity, "You can't collect points if Myo is not streaming!", Toast.LENGTH_SHORT).show()
    }

    override fun showCollectionStarted() {
        //button_start_collecting?.text = getString(R.string.stop_collecting)
    }

    override fun showCollectionStopped() {
        //button_start_collecting?.text = getString(R.string.start_collecting)
    }

    override fun showCollectedPoints(totalPoints: Int) {
        //points_count.text = totalPoints.toString()
    }

    override fun enableResetButton() {
        //button_reset_collecting.isEnabled = true
    }

    override fun disableResetButton() {
        //button_reset_collecting.isEnabled = false
    }

    override fun hideSaveArea() {
        /*button_save.visibility = View.INVISIBLE
        button_share.visibility = View.INVISIBLE
        save_export_title.visibility = View.INVISIBLE
        save_export_subtitle.visibility = View.INVISIBLE*/
    }

    override fun showSaveArea() {
        /*button_save.visibility = View.VISIBLE
        button_share.visibility = View.VISIBLE
        save_export_title.visibility = View.VISIBLE
        save_export_subtitle.visibility = View.VISIBLE*/
    }

    override fun addSignText(content : String){
        if(content.length > 0){
            MessageManager.getInstance().buildSignMessage()
        }
    }

    override fun sharePlainText(content: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun saveCsvFile(content: String) {
        context?.apply {
            val hasPermission = (ContextCompat.checkSelfPermission(this,
                    WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
            if (hasPermission) {
                writeToFile(content)
            } else {
                fileContentToSave = content
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL_CODE)
            }
        }
    }


    private fun writeToFile(content: String) {
        val storageDir =
                File("${Environment.getExternalStorageDirectory().absolutePath}/myo_emg")
        storageDir.mkdir()
        val outfile = File(storageDir, "myo_emg_export_${System.currentTimeMillis()}.csv")
        val fileOutputStream = FileOutputStream(outfile)
        fileOutputStream.write(content.toByteArray())
        fileOutputStream.close()
        Toast.makeText(activity, "Saved to: ${outfile.path}", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fileContentToSave?.apply { writeToFile(this) }
                } else {
                    Toast.makeText(activity, getString(R.string.write_permission_denied_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}