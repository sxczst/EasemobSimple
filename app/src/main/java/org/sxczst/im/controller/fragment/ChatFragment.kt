package org.sxczst.im.controller.fragment

import android.content.Intent
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.EaseUI
import com.hyphenate.easeui.ui.EaseConversationListFragment
import org.sxczst.im.controller.activity.ChatActivity

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 10:15
 * @Description :会话列表页面
 */
class ChatFragment : EaseConversationListFragment() {
    private var mEMMessageListener = object : EMMessageListener {
        override fun onMessageRecalled(p0: MutableList<EMMessage>?) {
        }

        override fun onMessageChanged(p0: EMMessage?, p1: Any?) {
        }

        override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {
        }

        override fun onMessageReceived(list: MutableList<EMMessage>?) {
            // 设置数据
            EaseUI.getInstance().notifier.notify(list)
            // 刷新页面
            refresh()
        }

        override fun onMessageDelivered(p0: MutableList<EMMessage>?) {
        }

        override fun onMessageRead(p0: MutableList<EMMessage>?) {
        }
    }

    override fun initView() {
        super.initView()
        // 监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener)

        // 跳转到会话详情页面
        setConversationListItemClickListener {
            val intent = Intent(activity, ChatActivity::class.java)

            // 传递参数
            intent.putExtra(EaseConstant.EXTRA_USER_ID, it.conversationId())

            // 是否是群聊
            if (it.type == EMConversation.EMConversationType.GroupChat) {
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
            }
            startActivity(intent)
        }

        // 解决会话列表页面同一会话出现多次的情况
        conversationList.clear()
    }
}