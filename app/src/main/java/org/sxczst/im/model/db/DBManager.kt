package org.sxczst.im.model.db

import android.content.Context
import org.sxczst.im.model.dao.ContactDao
import org.sxczst.im.model.dao.InviteDao

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 17:43
 * @Description :联系人表和邀请表的操作类的统一处理类
 */
class DBManager(context: Context, name: String) {

    /**
     * 关闭数据库的方法
     */
    fun close() {
        dbHelper.close()
    }

    var dbHelper = DBHelper(context, name)

    // 创建该数据库中两张表的操作类
    var contactDao = ContactDao(dbHelper)   // 联系人
    var inviteDao = InviteDao(dbHelper)     // 邀请
}