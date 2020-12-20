package org.sxczst.im.controller.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseChatFragment
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider
import org.sxczst.im.R
import org.sxczst.im.utils.Constants

/**
 * 会话详情页面
 */
class ChatActivity : AppCompatActivity() {
    /**
     * 监听退出群聊广播的接受者
     */
    private val mExitGroupReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getStringExtra(Constants.GROUP_ID) == mHxid) {
                // 结束当前页面
                finish()
            }
        }
    }

    /**
     * 当前的聊天类型
     */
    private var mChatType: Int? = null
    private var mHxid: String? = null
    private lateinit var easeChatFragment: EaseChatFragment

    /**
     * 获取本地广播管理者
     */
    private val mLBM: LocalBroadcastManager = LocalBroadcastManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initView()
        initData()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLBM.unregisterReceiver(mExitGroupReceiver)
    }

    /**
     * 初始化全局的监听
     */
    private fun initListener() {
        easeChatFragment.setChatFragmentHelper(object : EaseChatFragment.EaseChatFragmentHelper {
            override fun onAvatarClick(username: String?) {
            }

            override fun onMessageBubbleClick(message: EMMessage?): Boolean = false

            override fun onAvatarLongClick(username: String?) {
            }

            override fun onMessageBubbleLongClick(message: EMMessage?) {
            }

            override fun onSetCustomChatRowProvider(): EaseCustomChatRowProvider? = null

            override fun onSetMessageAttributes(message: EMMessage?) {
            }

            override fun onExtendMenuItemClick(itemId: Int, view: View?): Boolean = false

            /**
             * 进入会话详情页面
             */
            override fun onEnterToChatDetails() {
                val intent = Intent(this@ChatActivity, ChatDetailActivity::class.java)
                // 传递参数

                // 群Id
                intent.putExtra(Constants.GROUP_ID, mHxid)

                startActivity(intent)
            }
        })

        // 判断聊天类型
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            mLBM.registerReceiver(mExitGroupReceiver, IntentFilter(Constants.EXIT_GROUP))
        }
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
        easeChatFragment = EaseChatFragment()

        mHxid = intent.getStringExtra(EaseConstant.EXTRA_USER_ID)

        // 获取聊天类型
        mChatType = intent.extras?.getInt(EaseConstant.EXTRA_CHAT_TYPE)

        // 传递进行会话的用户信息
        easeChatFragment.arguments = intent.extras

        // 替换Fragment
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.fl_chat, easeChatFragment).commit()
    }
}