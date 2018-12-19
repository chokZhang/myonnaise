package com.mengyuan1998.finger_dancing.ui.scan

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import com.mengyuan1998.finger_dancing.BaseFragment
import com.mengyuan1998.finger_dancing.R
import com.mengyuan1998.finger_dancing.ui.model.Device
import kotlinx.android.synthetic.main.layout_scan_device.*
import javax.inject.Inject

private const val REQUEST_ACCESS_COARSE_LOCATION = 1
class ScanDeviceFragment : com.mengyuan1998.finger_dancing.BaseFragment<ScanDeviceContract.Presenter>(), ScanDeviceContract.View {

    companion object {
        fun newInstance() = ScanDeviceFragment()
    }

    private var listDeviceAdapter: DeviceAdapter? = null

    @Inject
    lateinit var scanDevicePresenter: ScanDevicePresenter

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        attachPresenter(scanDevicePresenter)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_scan_device, container, false)

        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_scan.setOnClickListener {

            requestPermission()

            //scanDevicePresenter.onScanToggleClicked()
        }

        listDeviceAdapter = DeviceAdapter(object : DeviceSelectedListener {
            override fun onDeviceSelected(v: View, position: Int) {
                scanDevicePresenter.onDeviceSelected(position)
            }
        })
        list_device_found.layoutManager = LinearLayoutManager(this.context)
        list_device_found.itemAnimator = FadeInAnimator()
        list_device_found.adapter = listDeviceAdapter
    }

    override fun showStartMessage() {
        text_empty_list?.text = getString(R.string.first_do_a_scan)
        text_empty_list?.visibility = View.VISIBLE
    }

    override fun showEmptyListMessage() {
        text_empty_list.text = getString(R.string.no_myo_found)
        text_empty_list.visibility = View.VISIBLE
    }

    override fun hideEmptyListMessage() {
        text_empty_list.visibility = View.INVISIBLE
    }

    override fun populateDeviceList(list: List<Device>) {
        listDeviceAdapter?.deviceList = list.toMutableList()
        listDeviceAdapter?.notifyDataSetChanged()
    }

    override fun addDeviceToList(device: Device) {
        listDeviceAdapter?.deviceList?.add(device)
        listDeviceAdapter?.notifyItemInserted(listDeviceAdapter?.itemCount ?: 0)
    }

    override fun wipeDeviceList() {
        listDeviceAdapter?.deviceList = mutableListOf()
        listDeviceAdapter?.notifyItemRangeRemoved(0, listDeviceAdapter?.itemCount ?: 0)
    }

    override fun showScanLoading() {
        progress_bar_search.animate().alpha(1.0f)
        fab_scan.setImageDrawable(context?.getDrawable(R.drawable.ic_stop))
    }

    override fun hideScanLoading() {
        progress_bar_search?.animate()?.alpha(0.0f)
        fab_scan?.setImageDrawable(context?.getDrawable(R.drawable.ic_magnify))
    }

    override fun showScanError() {
        Toast.makeText(this.context, getString(R.string.scan_failed), Toast.LENGTH_SHORT).show()
    }

    override fun showScanCompleted() {
        Toast.makeText(this.context, getString(R.string.scan_completed), Toast.LENGTH_SHORT).show()
    }

    override fun navigateToControlDevice() {
        //(activity as MainActivity).navigateToPage(1)
    }

    private fun requestPermission() {

        context?.apply {
            val hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            if(!hasPermission){
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_ACCESS_COARSE_LOCATION)
            }
            else{
                scanDevicePresenter.onScanToggleClicked()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_COARSE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanDevicePresenter.onScanToggleClicked()
                } else {
                    Toast.makeText(activity, getString(R.string.write_permission_denied_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    class DeviceAdapter(
            private val deviceSelectedListener: DeviceSelectedListener
    ) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

        var deviceList = mutableListOf<Device>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                DeviceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false), deviceSelectedListener)

        override fun getItemCount() = deviceList.size

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.bind(deviceList[position])
        }


        class DeviceViewHolder(
                val item: View,
                private val listener: DeviceSelectedListener
        ) : RecyclerView.ViewHolder(item) {
            fun bind(device: Device) {
                item.findViewById<TextView>(R.id.text_name).text =
                        device.name ?: item.context.getString(R.string.unknown_device)
                item.findViewById<TextView>(R.id.text_address).text = device.address
                item.findViewById<MaterialButton>(R.id.button_select).setOnClickListener {
                    listener.onDeviceSelected(it, adapterPosition)
                }
            }
        }
    }

    interface DeviceSelectedListener {
        fun onDeviceSelected(v: View, position: Int)
    }


    inner class FadeInAnimator : DefaultItemAnimator() {

        override fun animateAdd(viewHolder: RecyclerView.ViewHolder): Boolean {
            if (viewHolder is DeviceAdapter.DeviceViewHolder) {
                viewHolder.item.alpha = 0.0f
                viewHolder.itemView.animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                dispatchAddFinished(viewHolder)
                            }
                        })
                        .start()
            }
            return false
        }
    }
}