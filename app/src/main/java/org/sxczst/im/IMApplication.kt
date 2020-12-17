package org.sxczst.im

import android.app.Application
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
        options.acceptInvitationAlways = false
        options.isAutoAcceptGroupInvitation = false
        EaseUI.getInstance().init(this, options)
        Model.instance.initialize(this)
    }
}