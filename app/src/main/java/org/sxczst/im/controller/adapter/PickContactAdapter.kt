package org.sxczst.im.controller.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import org.sxczst.im.R
import org.sxczst.im.model.bean.PickContactInfo

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/19 21:40
 * @Description :选择联系人列表适配器
 */
class PickContactAdapter(
    var mContext: Context,
    var mPickContacts: List<PickContactInfo>
) : BaseAdapter() {
    /**
     * 群组中已经存在的成员
     */
    private var mExistMembers = mutableListOf<String>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 1. 创建或者获取ViewHolder
        var viewHolder: ViewHolder? = null
        var view: View? = null
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_pick_contact, null)
            viewHolder = ViewHolder(
                view.findViewById(R.id.cb_pick),
                view.findViewById(R.id.tv_pick_name)
            )
            view.tag = viewHolder
        }
        convertView?.tag?.let {
            viewHolder = it as ViewHolder
        }
        // 2. 获取当前Item数据
        val pickContactInfo = mPickContacts[position]
        // 3. 展示数据
        viewHolder?.checkBox?.isChecked = pickContactInfo.isChecked
        viewHolder?.name?.text = pickContactInfo.userInfo.name

        // 添加判断
        if (mExistMembers.contains(pickContactInfo.userInfo.hxid)) {
            viewHolder?.checkBox?.isChecked = true
            pickContactInfo.isChecked = true
        }

        // 4. 返回View
        return view ?: convertView ?: View(mContext)
    }

    override fun getItem(position: Int): Any = mPickContacts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mPickContacts.size

    /**
     * 获取选择的联系人
     */
    fun getPickContacts(): List<String> {
        val picks = mutableListOf<String>()
        mPickContacts.forEach { contact ->
            // 判断是否选中
            if (contact.isChecked) {
                contact.userInfo.name?.let { picks.add(it) }
            }
        }
        return picks
    }

    /**
     * 添加组成员信息
     */
    fun setGroupMembers(members: List<String>) {
        mExistMembers.clear()
        mExistMembers.addAll(members)
        notifyDataSetChanged()
    }

    inner class ViewHolder(var checkBox: CheckBox, var name: TextView)
}