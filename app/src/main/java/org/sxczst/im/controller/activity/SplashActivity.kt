package org.sxczst.im.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.hyphenate.chat.EMClient
import org.sxczst.im.R
import org.sxczst.im.model.Model

class SplashActivity : AppCompatActivity() {
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 如果当前Activity 已经退出，那么就不处理handler中的消息
            if (isFinishing) {
                return
            }
            // 判断进入主界面还是登录界面
            toMainOrLogin()
        }
    }

    /**
     * 判断进入主界面还是登录界面
     */
    private fun toMainOrLogin() {
        Model.instance.executors.execute {
            // 判断当前账号是否已经登录过
            if (EMClient.getInstance().isLoggedInBefore) {
                // 登录过，
                // 获取当前登录用户的信息
                val accountByHxId = Model.instance.userAccountDao
                    ?.getAccountByHxId(EMClient.getInstance().currentUser)
                if (accountByHxId == null) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    // 登录成功的逻辑
                    Model.instance.loginSuccess(accountByHxId)
                    // 跳转到主界面
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else {
                // 没有登录过

                // 跳转到登录界面
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        // 发送两秒钟的延时消息
        handler.sendMessageDelayed(Message.obtain(), 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 销毁消息
        handler.removeCallbacksAndMessages(null)
    }
}