package com.mengyuan1998.finger_dancing.ui.export

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechRecognizer
import com.iflytek.cloud.SpeechUtility
import dagger.android.support.AndroidSupportInjection
import com.mengyuan1998.finger_dancing.R
import com.mengyuan1998.finger_dancing.Utilities.MessageManager
import com.mengyuan1998.finger_dancing.Utilities.SoftKeyBoardListener
import com.mengyuan1998.finger_dancing.adpter.ConversationMessagesRVAdapter
import kotlinx.android.synthetic.main.layout_export.*
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


private const val REQUEST_WRITE_EXTERNAL_CODE = 2

class ExportFragment : com.mengyuan1998.finger_dancing.BaseFragment<ExportContract.Presenter>(), ExportContract.View {

    companion object {
        fun newInstance() = ExportFragment()
    }

    @Inject
    lateinit var exportPresenter: ExportPresenter

    private var fileContentToSave: String? = null

    private var isClicked : Boolean = false


    lateinit var adapter : ConversationMessagesRVAdapter

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            addVoiceText(msg?.obj.toString())
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

        adapter =  ConversationMessagesRVAdapter(activity)

        //键盘弹出事件监听
        SoftKeyBoardListener.setListener(activity, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener{
            override fun keyBoardShow(height: Int) {
                //弹出
                disImg()
            }

            override fun keyBoardHide(height: Int) {
                //消失
                showImg()
            }
        })
        setHasOptionsMenu(true)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCommunicate_img.setOnClickListener{
            if(!isClicked){

                System.out.println("exportPresent " + "click")

                exportPresenter.onConversationStart()

                showConversationPressed()
            }
            else {
                System.out.println("exportPresent " + "clicked")

                exportPresenter.onConversationStop()

                showConversationReset()
            }
            isClicked = !isClicked
        }

        conversation_display_rv.adapter = adapter
        conversation_display_rv.layoutManager = LinearLayoutManager(activity)



        send_conversation.setOnClickListener {
            var content = edit_conversation.text.toString();
            if(content.isNotEmpty()){
                MessageManager.getInstance().buildTextMessage(content)
            }
        }



        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=12345678")
        val speechRecognizer = SpeechRecognizer.createRecognizer(context) { code -> Log.d("", "SpeechRecognizer init() code = $code") }
        Log.d("abv", "onCreate: $speechRecognizer")



        MessageManager.getInstance().addNewNoticeTarget(object : MessageManager.NoticeMessageChanged {

            override fun onNewMessageAdd() {
                adapter.updateMessageList()
                //adapter.notifyDataSetChanged()
                adapter.notifyItemInserted(adapter.itemCount - 1)
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



    }

    override fun addSignText(content : String?){
        if(content != null){
            MessageManager.getInstance().buildSignMessage(content)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
        val outfile = File(storageDir, "myo_emg_export_${System.currentTimeMillis()}.txt")
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

    override fun showConversationPressed() {
        //开始交流被按下后的视图改变
        startCommunicate_img.setImageResource(R.drawable.myo_icon_on)
    }

    override fun showConversationReset() {
        //开始交流的重置
        startCommunicate_img.setImageResource(R.drawable.myo_icon)
    }

    override fun showSpeechPressed() {
        //开始说话被按下
    }

    override fun showSpeechReset() {
        //开始说话重置
    }

    override fun addVoiceText(content: String) {
        //向对话框中添加一段语音信息
    }

    override fun addEditText(content: String) {
        //将编辑框中的文字发送到对话框中
    }

    override fun cleanEditText(content: String) {
        //清空对话框的内容
        edit_conversation.text.clear()
    }

    override fun showNotStreamingErrorMessage() {
        Toast.makeText(activity, "You can't collect points if Myo is not streaming!", Toast.LENGTH_SHORT).show()
    }

    override fun showImg() {
        control_layout.visibility = View.VISIBLE
    }

    override fun disImg() {
        control_layout.visibility = View.INVISIBLE
    }
}