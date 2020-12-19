package org.sxczst.im.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseChatFragment
import com.superrtc.mediamanager.EMediaManager.initData
import org.sxczst.im.R

/**
 * 会话详情页面
 */
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initView()
        initData()
    }

    /**
     * 初始化布局
     */
    private fun initView() {

    }

    /**
     *
     */
    private fun initData() {
        // 创建一个会话的Fragment
        val easeChatFragment = EaseChatFragment()

        val mHxid = intent.getStringExtra(EaseConstant.EXTRA_USER_ID)

        // 传递进行会话的用户信息
        easeChatFragment.arguments = intent.extras

        // 替换Fragment
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.fl_chat, easeChatFragment).commit()
    }
}