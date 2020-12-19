package org.sxczst.im.controller.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import kotlinx.android.synthetic.main.activity_invite.*
import org.sxczst.im.R
import org.sxczst.im.controller.adapter.InviteAdapter
import org.sxczst.im.model.Model
import org.sxczst.im.model.bean.InvitationInfo
import org.sxczst.im.utils.Constants

/**
 * 邀请信息列表页面
 */
class InviteActivity : AppCompatActivity() {
    /**
     * 联系人邀请信息广播接受者
     */
    private val contactInviteChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refresh()
        }
    }

    /**
     * 本地广播管理者
     */
    private lateinit var mLBM: LocalBroadcastManager

    /**
     * 邀请信息列表适配器
     */
    private lateinit var adapter: InviteAdapter
    private val mOnInviteListener = object : InviteAdapter.OnInviteListener {
        /**
         * 通知环信服务器，点击了接受按钮
         */
        override fun onAccept(invitationInfo: InvitationInfo) {
            // 1.
            Model.instance.executors.execute {
                try {
                    val hxid = invitationInfo.user?.hxid
                    EMClient.getInstance().contactManager()
                        .acceptInvitation(hxid)

                    // 数据库更新
                    Model.instance.dbManager?.inviteDao?.updateInvitationStatus(
                        InvitationInfo.InviteStatus.INVITE_ACCEPT,
                        hxid
                    )

                    runOnUiThread {
                        // 页面更新
                        Toast.makeText(this@InviteActivity, "接受了邀请", Toast.LENGTH_SHORT).show()
                        refresh()
                    }

                } catch (e: HyphenateException) {
                    runOnUiThread {
                        Toast.makeText(this@InviteActivity, "接受邀请失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        /**
         * 通知环信服务器，点击了拒绝按钮
         */
        override fun onReject(invitationInfo: InvitationInfo) {
            Model.instance.executors.execute {
                val hxid = invitationInfo.user?.hxid
                try {
                    EMClient.getInstance().contactManager().declineInvitation(hxid)

                    // 数据库变化
                    Model.instance.dbManager?.inviteDao?.removeInvitation(hxid)

                    // 页面的变化
                    runOnUiThread {
                        Toast.makeText(this@InviteActivity, "拒绝了邀请", Toast.LENGTH_SHORT).show()
                        // 刷新页面
                        refresh()
                    }
                } catch (e: HyphenateException) {
                    Toast.makeText(this@InviteActivity, "拒绝邀请失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消广播
        mLBM.unregisterReceiver(contactInviteChangeReceiver)
    }

    private fun initData() {
        // 初始化ListView
        adapter = InviteAdapter(this, mOnInviteListener)
        lv_invite.adapter = adapter
        // 刷新数据
        refresh()

        // 动态注册邀请信息变化广播的接受者
        mLBM = LocalBroadcastManager.getInstance(this)
        mLBM.registerReceiver(
            contactInviteChangeReceiver,
            IntentFilter(Constants.CONTACT_INVITE_CHANGED)
        )
    }

    /**
     * 刷新数据
     */
    private fun refresh() {
        // 从数据库中获取邀请人信息
        val invitations = Model.instance.dbManager?.inviteDao?.getInvitations()
        // 刷新适配器
        adapter.refresh(invitations ?: listOf())
    }
}