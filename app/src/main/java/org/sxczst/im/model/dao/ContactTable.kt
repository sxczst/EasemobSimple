package org.sxczst.im.model.dao

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 15:42
 * @Description :联系人的建表语句
 */
class ContactTable {
    companion object {
        const val TAB_NAME = "tab_contact"

        const val COL_HXID = "hxid"
        const val COL_NAME = "name"
        const val COL_NICK = "nick"
        const val COL_PHOTO = "photo"

        const val COL_IS_CONTACT = "is_contact" //是否是联系人

        const val CREATE_TAB = "create table " +
                "$TAB_NAME (" +
                "$COL_HXID text primary key," +
                "$COL_NAME text," +
                "$COL_NICK text," +
                "$COL_PHOTO text," +
                "$COL_IS_CONTACT integer);"
    }
}