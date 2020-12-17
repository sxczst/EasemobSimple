package org.sxczst.im.model.dao

import android.content.ContentValues
import org.sxczst.im.model.bean.GroupInfo
import org.sxczst.im.model.bean.InvitationInfo
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.model.db.DBHelper

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 16:47
 * @Description :邀请表的操作类，增删改查
 */
class InviteDao(var dbHelper: DBHelper) {
    /**
     * 添加邀请
     */
    fun addInvitation(invitation: InvitationInfo) {
        // 获取数据库连接
        val values = ContentValues().apply {
            put(InviteTable.COL_REASON, invitation.reason)  //原因
            put(InviteTable.COL_STATUS, invitation.inviteStatus?.ordinal)    //状态

            invitation.user?.let {
                // 联系人
                put(InviteTable.COL_USER_HXID, it.hxid)
                put(InviteTable.COL_USER_NAME, it.name)
            }
            invitation.group?.let {
                //群组
                put(InviteTable.COL_GROUP_HXID, it.groupId)
                put(InviteTable.COL_GROUP_NAME, it.groupName)
                // TODO: 2020/10/31 将邀请人存在用户环信Id
                put(InviteTable.COL_USER_HXID, it.invitePerson)
            }
        }
        // 执行添加语句
        dbHelper.readableDatabase.replace(InviteTable.TAB_NAME, null, values)
    }

    /**
     * 获取所有邀请信息
     */
    fun getInvitations(): List<InvitationInfo> {
        // 获取数据库连接

        // 执行查询语句
        val sql = "select * from ${InviteTable.TAB_NAME}"
        val cursor = dbHelper.readableDatabase.rawQuery(sql, null)
        val invitationInfos = mutableListOf<InvitationInfo>()
        while (cursor.moveToNext()) {
            invitationInfos.add(InvitationInfo().apply {
                reason = cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON))    // 原因
                inviteStatus =
                    int2InvitationStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS)))  //状态

                if (cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID)).isEmpty()) {
                    // 联系人信息
                    user = UserInfo().apply {
                        hxid = cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID))
                        name = cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME))
                        nick = cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME))
                    }
                } else {
                    // 群组信息
                    group = GroupInfo().apply {
                        groupId =
                            cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID))
                        groupName =
                            cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME))
                        // TODO: 2020/10/31 从用户环信Id读取邀请人信息
                        invitePerson =
                            cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID))
                    }

                }

            })
        }
        // 关闭资源
        cursor.close()
        // 返回数据
        return invitationInfos
    }

    /**
     * 将int类型状态转换为邀请的状态
     */
    fun int2InvitationStatus(intStatus: Int): InvitationInfo.InviteStatus? {
        return when (intStatus) {
            InvitationInfo.InviteStatus.NEW_INVITE.ordinal -> InvitationInfo.InviteStatus.NEW_INVITE
            InvitationInfo.InviteStatus.INVITE_ACCEPT.ordinal -> InvitationInfo.InviteStatus.INVITE_ACCEPT
            InvitationInfo.InviteStatus.INVITE_ACCEPT_BY_PEER.ordinal -> InvitationInfo.InviteStatus.INVITE_ACCEPT_BY_PEER
            InvitationInfo.InviteStatus.NEW_GROUP_INVITE.ordinal -> InvitationInfo.InviteStatus.NEW_GROUP_INVITE
            InvitationInfo.InviteStatus.NEW_GROUP_APPLICATION.ordinal -> InvitationInfo.InviteStatus.NEW_GROUP_APPLICATION
            InvitationInfo.InviteStatus.GROUP_INVITE_ACCEPTED.ordinal -> InvitationInfo.InviteStatus.GROUP_INVITE_ACCEPTED
            InvitationInfo.InviteStatus.GROUP_APPLICATION_ACCEPTED.ordinal -> InvitationInfo.InviteStatus.GROUP_APPLICATION_ACCEPTED
            InvitationInfo.InviteStatus.GROUP_INVITE_DECLINED.ordinal -> InvitationInfo.InviteStatus.GROUP_INVITE_DECLINED
            InvitationInfo.InviteStatus.GROUP_APPLICATION_DECLINED.ordinal -> InvitationInfo.InviteStatus.GROUP_APPLICATION_DECLINED
            InvitationInfo.InviteStatus.GROUP_ACCEPT_INVITE.ordinal -> InvitationInfo.InviteStatus.GROUP_ACCEPT_INVITE
            InvitationInfo.InviteStatus.GROUP_ACCEPT_APPLICATION.ordinal -> InvitationInfo.InviteStatus.GROUP_ACCEPT_APPLICATION
            InvitationInfo.InviteStatus.GROUP_REJECT_INVITE.ordinal -> InvitationInfo.InviteStatus.GROUP_REJECT_INVITE
            InvitationInfo.InviteStatus.GROUP_REJECT_APPLICATION.ordinal -> InvitationInfo.InviteStatus.GROUP_REJECT_APPLICATION
            else -> null
        }
    }

    /**
     * 删除邀请
     */
    fun removeInvitation(hxId: String?) {
        hxId?.let {
            dbHelper.readableDatabase.delete(
                InviteTable.TAB_NAME, "${InviteTable.COL_USER_HXID}=?",
                arrayOf(it)
            )
        }
    }

    /**
     * 更新邀请状态
     */
    fun updateInvitationStatus(
        invitationStatus: InvitationInfo.InviteStatus,
        hxId: String?
    ) {
        hxId?.let {
            val values = ContentValues().apply {
                put(InviteTable.COL_STATUS, invitationStatus.ordinal)
            }
            dbHelper.readableDatabase.update(
                InviteTable.TAB_NAME,
                values,
                "${InviteTable.COL_USER_HXID}=?",
                arrayOf(it)
            )
        }
    }

}