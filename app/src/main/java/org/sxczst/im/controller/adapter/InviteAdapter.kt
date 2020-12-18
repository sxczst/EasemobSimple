package org.sxczst.im.controller.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.sxczst.im.R
import org.sxczst.im.model.bean.InvitationInfo

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/18 22:12
 * @Description :联系人列表适配器
 */
class InviteAdapter(var context: Context) : BaseAdapter() {
    /**
     * 存储邀请信息的集合
     */
    var mInvitationInfos = mutableListOf<InvitationInfo>()

    /**
     * 刷新数据的方法
     */
    fun refresh(invitationInfos: List<InvitationInfo>) {
        if (invitationInfos.isNotEmpty()) {
            mInvitationInfos.addAll(invitationInfos)
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 1. 获取或者创建一个ViewHolder
        var viewHolder: ViewHolder? = null
        if (convertView == null) {
            viewHolder = ViewHolder()
            var view = LayoutInflater.from(context).inflate(R.layout.item_invite, null)
        }
        // 2. 获取当前Item的数据
        // 3. 展示当前Item的数据
        // 4. 返回View
        return convertView!!
    }

    override fun getItem(position: Int): Any = mInvitationInfos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mInvitationInfos.size

    inner class ViewHolder

}