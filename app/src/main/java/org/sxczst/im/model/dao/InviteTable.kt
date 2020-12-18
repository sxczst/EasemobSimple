package org.sxczst.im.model.dao

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 16:39
 * @Description :邀请信息的表
 */
class InviteTable {
    companion object {
        const val TAB_NAME = "tab_invite"
        const val COL_USER_HXID = "user_hxid"   // 用户的环信Id
        const val COL_USER_NAME = "user_name"   // 用户的名称

        const val COL_GROUP_HXID = "group_hxid" // 群组的环信Id
        const val COL_GROUP_NAME = "group_name" // 群组名称

        const val COL_REASON = "reason"         // 邀请的原因
        const val COL_STATUS = "status"         // 邀请的状态

        const val CREATE_TAB = "create table $TAB_NAME ($COL_USER_HXID text primary key," +
                "$COL_USER_NAME text," +
                "$COL_GROUP_HXID text," +
                "$COL_GROUP_NAME text," +
                "$COL_REASON text," +
                "$COL_STATUS integer);"

    }
}