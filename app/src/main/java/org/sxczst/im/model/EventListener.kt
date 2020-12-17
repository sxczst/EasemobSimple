package org.sxczst.im.model

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.EMContactListener
import com.hyphenate.chat.EMClient
import org.sxczst.im.model.bean.InvitationInfo
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.utils.Constants
import org.sxczst.im.utils.SpUtils

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/17 21:04
 * @Description :全局事件监听类
 */
class EventListener(var mContext: Context) {
    /**
     *
     */
    val mLBM = LocalBroadcastManager.getInstance(mContext)

    /**
     * 联系人变化的监听
     */
    private val emContactListener = object : EMContactListener {
        /**
         * 接受到添加联系人邀请时执行
         */
        override fun onContactInvited(hxid: String?, reason: String?) {
            // 1. 数据更新
            val invitationInfo = InvitationInfo(hxid?.let {
                UserInfo(
                    it
                )
            }, reason, InvitationInfo.InviteStatus.NEW_INVITE)
            Model.instance.dbManager?.inviteDao?.addInvitation(invitationInfo)

            // 2. 发送邀请信息变化的广播
            mLBM.sendBroadcast(Intent(Constants.CONTACT_INVITE_CHANGED))

            // 3. 红点的处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)
        }

        /**
         * 联系人删除时执行
         */
        override fun onContactDeleted(hxid: String?) {
            // 1. 数据更新
            Model.instance.dbManager?.contactDao?.saveContact(hxid?.let { UserInfo(it) }, true)
            Model.instance.dbManager?.inviteDao?.removeInvitation(hxid)
            // 2. 发送联系人变化的广播
            mLBM.sendBroadcast(Intent(Constants.CONTACT_CHANGED))
        }

        /**
         * 添加好友邀请被同意时执行
         */
        override fun onFriendRequestAccepted(hxid: String?) {
            // 1. 数据更新
            val invitationInfo = InvitationInfo()
            invitationInfo.user = hxid?.let { UserInfo(it) }
            invitationInfo.inviteStatus = InvitationInfo.InviteStatus.INVITE_ACCEPT_BY_PEER
            Model.instance.dbManager?.inviteDao?.addInvitation(invitationInfo)

            // 2. 发送邀请信息变化的广播
            mLBM.sendBroadcast(Intent(Constants.CONTACT_INVITE_CHANGED))

            // 3. 红点的处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)
        }

        /**
         * 联系人增加后执行
         */
        override fun onContactAdded(hxid: String?) {
            // 1. 数据更新
            Model.instance.dbManager?.contactDao?.saveContact(hxid?.let { UserInfo(it) }, true)

            // 2. 发送联系人变化的广播
            mLBM.sendBroadcast(Intent(Constants.CONTACT_CHANGED))
        }

        /**
         * 添加好友邀请被拒绝时执行
         */
        override fun onFriendRequestDeclined(p0: String?) {
            // 1. 红点的处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 2. 发送邀请信息变化的广播
            mLBM.sendBroadcast(Intent(Constants.CONTACT_INVITE_CHANGED))
        }
    }

    init {
        // 注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener)
    }
}