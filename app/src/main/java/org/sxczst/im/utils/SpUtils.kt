package org.sxczst.im.utils

import android.content.Context
import android.content.SharedPreferences
import org.sxczst.im.IMApplication

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/17 21:30
 * @Description :数据存取工具类
 */
class SpUtils private constructor() {
    /**
     * 保存数据
     */
    fun save(key: String, value: Any) {
        when (value) {
            is String -> {
                mSp?.edit()?.putString(key, value)?.apply()
            }
            is Boolean -> {
                mSp?.edit()?.putBoolean(key, value)?.apply()
            }
            is Int -> {
                mSp?.edit()?.putInt(key, value)?.apply()
            }
        }
    }

    fun getString(key: String, defValue: String) = mSp?.getString(key, defValue)

    fun getBoolean(key: String, defValue: Boolean) = mSp?.getBoolean(key, defValue)

    fun getInt(key: String, defValue: Int) = mSp?.getInt(key, defValue)

    companion object {
        private var mSp: SharedPreferences? = null
            get() {
                if (field == null) {
                    field = IMApplication.getGlobalApplication()
                        .getSharedPreferences("im", Context.MODE_PRIVATE)
                }
                return field
            }

        // 单例
        var instance: SpUtils? = null
            get() {
                if (field == null) {
                    field = SpUtils()
                }
                return field
            }

        const val IS_NEW_INVITE = "is_new_invite"
    }

}