package org.sxczst.im.model

import android.content.Context
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.model.dao.UserAccountDao
import org.sxczst.im.model.db.DBManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/27 18:07
 * @Description :数据模型层全局类
 */
class Model private constructor() {

    private var mContext: Context? = null

    // 创建用户账号数据库的操作类对象
    var userAccountDao = mContext?.let { UserAccountDao(it) }
    var dbManager: DBManager? = null

    // 全局线程池
    var executors: ExecutorService = Executors.newCachedThreadPool()

    private object ModelHolder {
        val holder = Model()
    }

    fun initialize(context: Context) {
        mContext = context

        // 开启全局的监听
        EventListener(context)
    }

    /**
     * 用户登录成功后的处理方法
     */
    fun loginSuccess(account: UserInfo) {
        dbManager?.close()
        dbManager = mContext?.let {
            account.name?.let { name ->
                DBManager(it, name)
            }
        }
    }

    companion object {
        val instance = ModelHolder.holder
    }

}