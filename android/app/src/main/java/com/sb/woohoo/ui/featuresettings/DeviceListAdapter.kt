package com.sb.woohoo.ui.featuresettings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sb.woohoo.R

class DeviceListAdapter(val context: Context, var dataSource: List<DeviceModel>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.style_device_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.tvDeviceId.text = dataSource.get(position).testDeviceId
        vh.tvEmailId.text = dataSource.get(position).emailId

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    fun getPosition(value: String): Int {
        var position: Int = 0
        dataSource.forEachIndexed { index, deviceModel ->
            if(value.contentEquals(deviceModel.testDeviceId))
                position = index;
        }
        return position
    }

    private class ItemHolder(row: View?) {
        val tvDeviceId: TextView
        val tvEmailId: TextView

        init {
            tvDeviceId = row?.findViewById(R.id.tv_device_id) as TextView
            tvEmailId = row.findViewById(R.id.tv_email_id) as TextView
        }
    }
}