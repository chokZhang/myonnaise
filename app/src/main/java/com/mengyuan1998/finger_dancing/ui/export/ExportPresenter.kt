package com.mengyuan1998.finger_dancing.ui.export

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.mengyuan1998.finger_dancing.dagger.DeviceManager
import com.ncorti.myonnaise.CommandList
import com.zhy.http.okhttp.OkHttpUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType

import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call


class ExportPresenter(
        override val view: ExportContract.View,
        private val deviceManager: DeviceManager
) : ExportContract.Presenter(view) {



    private val counter: AtomicInteger = AtomicInteger()
    private val buffer: ArrayList<DoubleArray> = arrayListOf()
    private val buffer2: ArrayList<DoubleArray> = arrayListOf()
    private val buffer3: ArrayList<DoubleArray> = arrayListOf()

    internal var dataSubscription: Disposable? = null


    override fun create() {}

    override fun start() {
        deviceManager.myo?.apply {
            if (!this.isStreaming()) {
                this.sendCommand(CommandList.emgFilteredOnly())
            }
        }
        deviceManager.myo?.apply {
            if (!this.isStreaming()) {
                //TODO
                //view.disableStartCollectingButton()

            }
        }
    }

    override fun stop() {
        dataSubscription?.dispose()

    }

    override fun onConversationStart() {

        counter.set(0)
        buffer.clear()
        buffer2.clear()
        buffer3.clear()
        deviceManager.myo?.apply {
            if (this.isStreaming()) {
                if (dataSubscription == null || dataSubscription?.isDisposed == true) {
                    dataSubscription = this.dataFlowable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe {
                            }
                            .subscribe {
                                if (it.size == 8)
                                    buffer.add(it)
                                else if(it.size == 6) {
                                    var result1: DoubleArray = doubleArrayOf(it[0],it[1],it[2])
                                    var result2: DoubleArray = doubleArrayOf(it[3],it[4],it[5])
                                    buffer2.add(result1)
                                    buffer3.add(result2)
                                }
                            }
                } else {
                    dataSubscription?.dispose()

                }
            } else {
                view.showNotStreamingErrorMessage()
            }
        }
    }


    override fun onConversationStop() {
        // HttpThread(createCsv(buffer,""),createCsv(buffer2,""),createCsv(buffer2,"")).start()
        OkHttpUtils
                .postString()
                .url("https://www.chekehome.com/public/index.php/helpfuc")
                .content(Gson().toJson(Httpdata(createCsv(buffer,""), createCsv(buffer2,""),createCsv(buffer2,""))))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(object : StringCallback() {
                    override fun onResponse(response: String?, id: Int) {
                        Log.d("exportPresent", "get in onSavePressed");
                        print(response)
                        //TODO 显示在recyclerview中
                        view.addSignText(response);
                    }

                    override fun onError(call: Call?, e: java.lang.Exception?, id: Int) {
                        print(e.toString())
                    }
                })
        view.saveCsvFile(createCsv(buffer,"emg\r\n"))
        view.saveCsvFile(createCsv(buffer2,"acc\r\n"))
        view.saveCsvFile(createCsv(buffer3,"gyr\r\n"))
    }

    override fun onSpeechStart() {
        //开始说话
    }

    override fun onSpeechStop() {
        //停止说话
    }

    @VisibleForTesting
    internal fun createCsv(buffer: ArrayList<DoubleArray>,index:String):String{
        val stringBuilder = StringBuilder()
        buffer.forEach {
            it.forEach {
                stringBuilder.append(it)
                stringBuilder.append(" ")
            }
            stringBuilder.append("\r\n")
        }
        return index+stringBuilder.toString()
    }


    inner class Httpdata {
        var emg: String = ""
        var acc: String = ""
        var gyro: String = ""
        constructor(emg: String,acc: String, gyro: String){
            this.emg = emg   //构造方法传值
            this.acc = acc
            this.gyro = gyro
        }
    }
}