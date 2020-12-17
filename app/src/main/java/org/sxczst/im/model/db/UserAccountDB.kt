package org.sxczst.im.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.sxczst.im.model.dao.UserAccountTable

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 8:23
 * @Description :
 */
class UserAccountDB(
    context: Context
) : SQLiteOpenHelper(context, "account.db", null, 1) {

    // 数据库创建时调用
    override fun onCreate(db: SQLiteDatabase?) {
        // 执行创建数据库表的语句
        db?.execSQL(UserAccountTable.CREATE_TAB)
    }

    // 数据库更新的时候调用
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}