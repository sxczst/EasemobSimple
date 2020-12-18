package org.sxczst.im.controller.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.ImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.easeui.ui.EaseContactListFragment
import kotlinx.android.synthetic.main.header_fragment_contact.*
import org.sxczst.im.R
import org.sxczst.im.controller.activity.AddContactActivity
import org.sxczst.im.controller.activity.InviteActivity
import org.sxczst.im.utils.Constants
import org.sxczst.im.utils.SpUtils

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 10:15
 * @Description :联系人列表页面
 */
class ContactFragment : EaseContactListFragment() {

    /**
     * 联系人邀请信息改变广播接受者
     */
    private val contactInviteChangeReceiver = object : BroadcastReceiver() {
        /**
         * 接收到广播
         */
        override fun onReceive(context: Context?, intent: Intent?) {
            // 更新红点显示
            ivContactRed?.visibility = View.VISIBLE
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, true)
        }
    }

    private var mLBM: LocalBroadcastManager? = null

    /**
     * 红点对象
     */
    private var ivContactRed: ImageView? = null

    override fun initView() {
        super.initView()

        // 布局显示加号
        titleBar.setRightImageResource(R.drawable.ease_type_select_btn_nor)

        // 添加 headerView
        val headerView = View.inflate(activity, R.layout.header_fragment_contact, null)
        listView.addHeaderView(headerView)

        // 获取红点对象
        ivContactRed = headerView.findViewById(R.id.iv_contact_red)

        // 获取邀请信息条目对象，设置点击事件
        ll_contact_invite.setOnClickListener {
            // 1. 红点处理
            ivContactRed?.visibility = View.GONE
            SpUtils.instance?.save(SpUtils.IS_NEW_INVITE, false)
            // 2. 跳转到邀请信息列表页面
            startActivity(Intent(activity, InviteActivity::class.java))
        }

    }

    override fun setUpView() {
        super.setUpView()

        // 添加按钮的点击事件
        titleBar.setRightLayoutClickListener {
            activity?.startActivity(Intent(activity, AddContactActivity::class.java))
        }

        // 初始化红点的显示
        val isNewInvite = SpUtils.instance?.getBoolean(SpUtils.IS_NEW_INVITE, false)
        isNewInvite?.let {
            ivContactRed?.visibility = if (it) View.VISIBLE else View.GONE
        }

        // 注册广播
        mLBM = activity?.let { LocalBroadcastManager.getInstance(it) }

        // 动态注册广播
        mLBM?.registerReceiver(
            contactInviteChangeReceiver,
            IntentFilter(Constants.CONTACT_INVITE_CHANGED)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭广播接受器
        mLBM?.unregisterReceiver(contactInviteChangeReceiver)
    }
}