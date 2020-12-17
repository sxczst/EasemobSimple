package org.sxczst.im.model.dao

import android.content.ContentValues
import android.content.Context
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.model.db.UserAccountDB

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 8:36
 * @Description :用户账号数据库的操作类
 */
class UserAccountDao(context: Context) {
    private val mHelper: UserAccountDB = UserAccountDB(context)

    /**
     * 添加用户到数据库
     */
    fun addAccount(user: UserInfo) {
        // 获取数据库对象
        val db = mHelper.readableDatabase
        // 执行添加操作
        val values = ContentValues()
        values.put(UserAccountTable.COL_HXID, user.hxid)
        values.put(UserAccountTable.COL_NAME, user.name)
        values.put(UserAccountTable.COL_NICK, user.nick)
        values.put(UserAccountTable.COL_PHOTO, user.photo)
        db.replace(UserAccountTable.TAB_NAME, null, values)
    }

    /**
     * 根据环信id获取所有用户信息
     */
    fun getAccountByHxId(hxId: String): UserInfo? {
        // 获取数据库对象
        val db = mHelper.readableDatabase

        // 执行查询语句
        val sql =
            "select * from ${UserAccountTable.TAB_NAME} where ${UserAccountTable.COL_HXID} = ?"
        val cursor = db.rawQuery(sql, arrayOf(hxId))
        var userInfo: UserInfo? = null
        if (cursor.moveToNext()) {
            userInfo = UserInfo()
            userInfo.hxid = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID))
            userInfo.name = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME))
            userInfo.nick = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK))
            userInfo.photo = cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO))
        }

        // 关闭资源
        cursor.close()

        // 返回数据
        return userInfo
    }
}