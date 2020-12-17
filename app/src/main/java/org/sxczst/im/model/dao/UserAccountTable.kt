package org.sxczst.im.model.dao

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 8:27
 * @Description :
 */
class UserAccountTable {

    companion object {
        const val TAB_NAME = "tab_account"
        const val COL_NAME = "name"
        const val COL_HXID = "hxid"
        const val COL_NICK = "nick"
        const val COL_PHOTO = "photo"
        const val CREATE_TAB = "create table " +
                "$TAB_NAME (" +
                "$COL_HXID text primary key," +
                "$COL_NAME text," +
                "$COL_NICK text," +
                "$COL_PHOTO text);"
    }
}