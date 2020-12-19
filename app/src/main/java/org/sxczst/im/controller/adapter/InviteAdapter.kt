package org.sxczst.im.controller.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import org.sxczst.im.R
import org.sxczst.im.model.bean.InvitationInfo

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/18 22:12
 * @Description :联系人列表适配器
 */
class InviteAdapter(
    var context: Context,
    var mOnInviteListener: OnInviteListener
) : BaseAdapter() {
    /**
     * 存储邀请信息的集合
     */
    private var mInvitationInfos = mutableListOf<InvitationInfo>()


    /**
     * 刷新数据的方法
     */
    fun refresh(invitationInfos: List<InvitationInfo>) {
        mInvitationInfos.clear()
        mInvitationInfos.addAll(invitationInfos)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 1. 获取或者创建一个ViewHolder
        var viewHolder: ViewHolder? = null
        var view: View? = null
        if (convertView == null) {
            // 1.1 创建
            viewHolder = ViewHolder()
            view = LayoutInflater.from(context).inflate(R.layout.item_invite, null)
            viewHolder.name = view.findViewById(R.id.tv_invite_name)
            viewHolder.reason = view.findViewById(R.id.tv_invite_reason)
            viewHolder.accept = view.findViewById(R.id.btn_invite_accept)
            viewHolder.reject = view.findViewById(R.id.btn_invite_reject)
            view.tag = viewHolder
        }
        // 1.2 获取
        viewHolder = convertView?.tag?.let {
            it as ViewHolder
        }

        // 2. 获取当前Item的数据
        val invitationInfo = mInvitationInfos[position]

        // 3. 展示当前Item的数据
        if (invitationInfo.user != null) {
            // 联系人邀请
            // 名称展示
            viewHolder?.name?.text = invitationInfo.user?.name
            // 按钮初始状态为隐藏
            viewHolder?.accept?.visibility = View.GONE
            viewHolder?.reject?.visibility = View.GONE
            // 原因展示
            when (invitationInfo.inviteStatus) {
                InvitationInfo.InviteStatus.NEW_INVITE -> {
                    // 新的邀请
                    viewHolder?.reason?.text = invitationInfo.reason ?: "添加好友"
                    viewHolder?.accept?.visibility = View.VISIBLE
                    viewHolder?.reject?.visibility = View.VISIBLE
                }
                InvitationInfo.InviteStatus.INVITE_ACCEPT -> {
                    // 接受邀请
                    viewHolder?.reason?.text = invitationInfo.reason ?: "接受邀请"
                }
                InvitationInfo.InviteStatus.INVITE_ACCEPT_BY_PEER -> {
                    // 邀请被接受
                    viewHolder?.reason?.text = invitationInfo.reason ?: "接受了你的邀请"
                }
                else -> {

                }
            }
            // 按钮的事件处理
            viewHolder?.accept?.setOnClickListener {
                mOnInviteListener.onAccept(invitationInfo)
            }
            viewHolder?.reject?.setOnClickListener {
                mOnInviteListener.onReject(invitationInfo)
            }
        } else {
            // 群组邀请
        }
        // 4. 返回View
        return view ?: convertView ?: View(context)
    }

    override fun getItem(position: Int): Any = mInvitationInfos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mInvitationInfos.size

    inner class ViewHolder {
        var name: TextView? = null
        var reason: TextView? = null

        var accept: Button? = null
        var reject: Button? = null
    }

    interface OnInviteListener {
        /**
         * 联系人接受按钮的点击事件
         */
        fun onAccept(invitationInfo: InvitationInfo)

        /**
         * 联系人拒绝按钮的点击事件
         *
         */
        fun onReject(invitationInfo: InvitationInfo)
    }
}