package org.sxczst.im.model.dao

import android.content.ContentValues
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.model.db.DBHelper

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 15:50
 * @Description :联系人表的操作类
 */
class ContactDao(
    var dbHelper: DBHelper
) {
    /**
     * 获取所有联系人
     */
    fun getContacts(): List<UserInfo> {
        // 获取数据库连接
        dbHelper.readableDatabase

        // 执行查询语句
        val sql = "select * from ${ContactTable.TAB_NAME} where ${ContactTable.COL_IS_CONTACT} = 1"
        val cursor = dbHelper.readableDatabase.rawQuery(sql, null)
        val userInfos = mutableListOf<UserInfo>()
        while (cursor.moveToNext()) {
            val userInfo = UserInfo()

            userInfo.hxid = cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID))
            userInfo.name = cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME))
            userInfo.nick = cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK))
            userInfo.photo = cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO))

            userInfos.add(userInfo)
        }
        // 关闭连接
        cursor.close()
        // 返回数据
        return userInfos
    }

    /**
     * 通过环信Id获取联系人单个信息
     */
    fun getContactByHx(hxId: String?): UserInfo? {
        return hxId?.let {
            // 获取数据库连接
            dbHelper.readableDatabase
            // 执行查询语句
            val sql = "select * from ${ContactTable.TAB_NAME} where ${ContactTable.COL_HXID}=?"
            val cursor = dbHelper.readableDatabase.rawQuery(sql, arrayOf(it))
            var userInfo: UserInfo? = null
            if (cursor.moveToNext()) {
                userInfo = UserInfo()
                userInfo.hxid = cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID))
                userInfo.name = cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME))
                userInfo.nick = cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK))
                userInfo.photo = cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO))
            }
            // 关闭资源
            cursor.close()
            // 返回数据
            userInfo
        }
    }

    /**
     * 通过环信Id获取用户联系人信息
     */
    fun getContactsByHx(hxIds: List<String>?): List<UserInfo>? {
        if (hxIds == null || hxIds.isEmpty()) {
            return null
        }
        val userInfos = mutableListOf<UserInfo>()
        // 遍历hxIds
        for (hxId in hxIds) {
            val userInfo = getContactByHx(hxId)
            userInfo?.let { userInfos.add(it) }
        }
        // 返回查询的数据
        return userInfos
    }

    /**
     * 保存单个联系人
     */
    fun saveContact(user: UserInfo?, isMyContact: Boolean) {
        user?.let {
            // 获取数据库连接
            dbHelper.readableDatabase
            // 执行保存语句
            val values = ContentValues().apply {
                put(ContactTable.COL_HXID, user.hxid)
                put(ContactTable.COL_NAME, user.name)
                put(ContactTable.COL_NICK, user.nick)
                put(ContactTable.COL_PHOTO, user.photo)
                put(ContactTable.COL_IS_CONTACT, if (isMyContact) 1 else 0)
            }
            dbHelper.readableDatabase.replace(
                ContactTable.TAB_NAME,
                null,
                values
            )
        }
    }

    /**
     * 保存联系人信息
     */
    fun saveContacts(contacts: List<UserInfo>?, isMyContact: Boolean) {
        contacts?.let {
            for (contact in contacts) {
                saveContact(contact, isMyContact)
            }
        }
    }

    /**
     * 删除联系人信息
     */
    fun deleteContactByHxId(hxId: String?) {
        hxId?.let {
            // 获取数据库连接 , 执行删除
            dbHelper.readableDatabase.delete(
                ContactTable.TAB_NAME,
                "$ContactTable.COL_HXID=?",
                arrayOf(hxId)
            )
        }
    }

}