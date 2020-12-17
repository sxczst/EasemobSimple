package org.sxczst.im.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.sxczst.im.model.dao.ContactTable
import org.sxczst.im.model.dao.InviteTable

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 15:39
 * @Description :
 */
class DBHelper(context: Context, name: String) : SQLiteOpenHelper(
    context,
    name,
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // 创建联系人的表
        db?.execSQL(ContactTable.CREATE_TAB)
        // 创建邀请信息的表
        db?.execSQL(InviteTable.CREATE_TAB)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}