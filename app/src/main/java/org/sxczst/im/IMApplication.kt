package org.sxczst.im

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.hyphenate.chat.EMOptions
import com.hyphenate.easeui.EaseUI
import org.sxczst.im.model.Model

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/27 16:41
 * @Description :
 */
class IMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val options = EMOptions()
        // 设置需要同意后才能接受邀请
        options.acceptInvitationAlways = false
        // 设置需要同意后才能接受群邀请
        options.isAutoAcceptGroupInvitation = false
        EaseUI.getInstance().init(this, options)
        Model.instance.initialize(this)
        // 初始化全局上下文
        mContext = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 配置为生成多个DEX。
        MultiDex.install(this)
    }

    companion object {
        private lateinit var mContext: Context

        /**
         * 获取全局的上下文对象
         */
        fun getGlobalApplication() = mContext
    }
}