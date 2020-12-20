package org.sxczst.im.model

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.EMContactListener
import com.hyphenate.chat.EMClient
import com.hyphenate.easeui.ui.EaseGroupListener
import org.sxczst.im.model.bean.GroupInfo
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

    /**
     * 群组信息变化的监听
     */
    private val eMGroupChangeListener = object : EaseGroupListener() {
        /**
         * 群聊被解散
         */
        override fun onGroupDestroyed(p0: String?, p1: String?) {

        }

        /**
         *
         */
        override fun onUserRemoved(p0: String?, p1: String?) {

        }

        /**
         * 收到群邀请
         */
        override fun onInvitationReceived(
            groupId: String?,
            groupName: String?,
            inviter: String?,
            reason: String?
        ) {
            super.onInvitationReceived(groupId, groupName, inviter, reason)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.reason = reason
            invitation.group = GroupInfo(groupName, groupId, inviter)
            invitation.inviteStatus = InvitationInfo.InviteStatus.NEW_GROUP_INVITE
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))

        }

        /**
         * 群邀请被别人同意
         */
        override fun onInvitationAccepted(groupId: String?, inviter: String?, reason: String?) {
            super.onInvitationAccepted(groupId, inviter, reason)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.reason = reason
            invitation.group = GroupInfo(groupId, groupId, inviter)
            invitation.inviteStatus = InvitationInfo.InviteStatus.GROUP_INVITE_ACCEPTED
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))
        }

        /**
         * 别人拒绝了您的群邀请
         */
        override fun onInvitationDeclined(groupId: String?, invitee: String?, reason: String?) {
            super.onInvitationDeclined(groupId, invitee, reason)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.reason = reason
            invitation.group = GroupInfo(groupId, groupId, invitee)
            invitation.inviteStatus = InvitationInfo.InviteStatus.GROUP_INVITE_DECLINED
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))
        }

        /**
         * 收到申请加入群信息
         */
        override fun onRequestToJoinReceived(
            groupId: String?,
            groupName: String?,
            applyer: String?,
            reason: String?
        ) {
            super.onRequestToJoinReceived(groupId, groupName, applyer, reason)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.reason = reason
            invitation.group = GroupInfo(groupName, groupId, applyer)
            invitation.inviteStatus = InvitationInfo.InviteStatus.NEW_GROUP_APPLICATION
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))
        }

        /**
         * 申请加入群聊被接受
         */
        override fun onRequestToJoinAccepted(
            groupId: String?,
            groupName: String?,
            accepter: String?
        ) {
            super.onRequestToJoinAccepted(groupId, groupName, accepter)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.group = GroupInfo(groupName, groupId, accepter)
            invitation.inviteStatus = InvitationInfo.InviteStatus.GROUP_APPLICATION_ACCEPTED
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))
        }

        /**
         * 申请加入群聊被拒绝
         */
        override fun onRequestToJoinDeclined(
            groupId: String?,
            groupName: String?,
            decliner: String?,
            reason: String?
        ) {
            super.onRequestToJoinDeclined(groupId, groupName, decliner, reason)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.reason = reason
            invitation.group = GroupInfo(groupName, groupId, decliner)
            invitation.inviteStatus = InvitationInfo.InviteStatus.GROUP_APPLICATION_DECLINED
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))
        }

        /**
         * 收到 群邀请被自动接受
         */
        override fun onAutoAcceptInvitationFromGroup(
            groupId: String?,
            inviter: String?,
            inviteMessage: String?
        ) {
            super.onAutoAcceptInvitationFromGroup(groupId, inviter, inviteMessage)
            // 1. 数据更新
            val invitation = InvitationInfo()
            invitation.reason = inviteMessage
            invitation.group = GroupInfo(groupId, groupId, inviter)
            invitation.inviteStatus = InvitationInfo.InviteStatus.GROUP_INVITE_ACCEPTED
            Model.instance.dbManager?.inviteDao?.addInvitation(invitation)

            // 2. 红点处理
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)

            // 3. 发送广播
            mLBM.sendBroadcast(Intent(Constants.GROUP_INVITE_CHANGED))
        }
    }

    init {
        // 注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener)

        // 注册一个群组信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(eMGroupChangeListener)
    }
}