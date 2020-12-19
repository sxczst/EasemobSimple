package org.sxczst.im.controller.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hyphenate.chat.EMGroup
import org.sxczst.im.R

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/19 14:33
 * @Description :群组列表适配器
 */
class GroupListAdapter(var context: Context) : BaseAdapter() {
    /**
     * 要展示的数据
     */
    var mGroups = mutableListOf<EMGroup>()

    /**
     * 刷新数据
     */
    fun refresh(groups: List<EMGroup>) {
        mGroups.clear()
        mGroups.addAll(groups)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 1. 创建或者获取ViewHolder
        var viewHolder: ViewHolder? = null
        var view: View? = null
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_group_list, null)
            viewHolder = ViewHolder(view.findViewById(R.id.tv_group_list_name))
            view.tag = viewHolder
        }
        viewHolder = convertView?.tag?.let {
            it as ViewHolder
        }

        // 2. 获取当前Item数据
        val emGroup = mGroups[position]

        // 3. 展示数据
        viewHolder?.name?.text = emGroup.groupName

        // 4. 返回View
        return view ?: convertView ?: View(context)
    }

    override fun getItem(position: Int): Any = mGroups[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mGroups.size

    inner class ViewHolder(var name: TextView)
}