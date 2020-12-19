package org.sxczst.im.controller.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.chat.EMClient
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.ui.EaseContactListFragment
import com.hyphenate.exceptions.HyphenateException
import kotlinx.android.synthetic.main.header_fragment_contact.*
import org.sxczst.im.R
import org.sxczst.im.controller.activity.AddContactActivity
import org.sxczst.im.controller.activity.ChatActivity
import org.sxczst.im.controller.activity.GroupListActivity
import org.sxczst.im.controller.activity.InviteActivity
import org.sxczst.im.model.Model
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.utils.Constants
import org.sxczst.im.utils.SpUtils

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 10:15
 * @Description :联系人列表页面
 */
class ContactFragment : EaseContactListFragment() {

    /**
     *
     */
    private var mHxid: String = ""

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

    /**
     * 联系人信息改变广播接受者
     */
    private val contactChangeReceiver = object : BroadcastReceiver() {
        /**
         * 接收到广播
         */
        override fun onReceive(context: Context?, intent: Intent?) {
            // 刷新联系人列表
            refreshContact()
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

        // 点击联系人条目进入会话详情页面
        setContactListItemClickListener { easeUser ->
            easeUser?.let {
                val intent = Intent(activity, ChatActivity::class.java)
                // 传递参数信息
                intent.putExtra(EaseConstant.EXTRA_USER_ID, easeUser.username)
                startActivity(intent)
            }
        }

        // 跳转到群组列表页面
        ll_contact_group.setOnClickListener {
            startActivity(Intent(activity, GroupListActivity::class.java))
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
        // 用于接受邀请信息发生改变的广播
        mLBM?.registerReceiver(
            contactInviteChangeReceiver,
            IntentFilter(Constants.CONTACT_INVITE_CHANGED)
        )
        // 用于接收联系人发生改变的广播
        mLBM?.registerReceiver(
            contactChangeReceiver,
            IntentFilter(Constants.CONTACT_CHANGED)
        )

        // 从环信服务器获取所有的联系人信息
        getContactListFromHxServer()

        // 绑定ListView和ContextMenu
        registerForContextMenu(listView)
    }

    /**
     * 创建布局
     */
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // 获取选中的条目信息
        val easeUser =
            listView.getItemAtPosition((menuInfo as AdapterView.AdapterContextMenuInfo).position) as EaseUser
        mHxid = easeUser.username

        // 添加布局
        activity?.menuInflater?.inflate(R.menu.delete, menu)
    }

    /**
     * 当条目被选中时的操作
     */
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.contact_delete -> {
                // 执行删除选中的联系人操作
                deleteContact()
                return true
            }
            else -> {
            }
        }
        return super.onContextItemSelected(item)
    }

    /**
     * 执行删除选中的联系人操作
     */
    private fun deleteContact() {
        // 从环信服务器上删除
        Model.instance.executors.execute {
            try {
                EMClient.getInstance().contactManager().deleteContact(mHxid)

                // 从本地数据库中删除
                Model.instance.dbManager?.contactDao?.deleteContactByHxId(mHxid)

                // 从内存(页面)中删除
                activity?.runOnUiThread {
                    Toast.makeText(activity, "删除$mHxid 成功", Toast.LENGTH_SHORT)
                        .show()
                    refreshContact()
                }
            } catch (e: HyphenateException) {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "删除$mHxid 失败", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    /**
     * 从环信服务器获取所有的联系人信息
     */
    private fun getContactListFromHxServer() {
        Model.instance.executors.execute {
            try {
                // 获取到所有的好友信息
                val hxids =
                    EMClient.getInstance().contactManager().allContactsFromServer

                // 校验
                if (hxids.isNotEmpty()) {

                    // 转换
                    val contacts = mutableListOf<UserInfo>()
                    hxids.forEach { hxid ->
                        contacts.add(UserInfo(hxid))
                    }

                    // 保存好友信息到本地的数据库中
                    Model.instance.dbManager?.contactDao?.saveContacts(contacts, true)

                    // 刷新页面
                    activity?.runOnUiThread {
                        // 刷新页面上联系人信息
                        refreshContact()
                    }
                }
            } catch (e: HyphenateException) {

            }

        }
    }

    /**
     * 刷新页面上联系人信息
     */
    private fun refreshContact() {
        // 1. 获取数据
        val contacts = Model.instance.dbManager?.contactDao?.getContacts()

        // 2. 校验
        contacts?.let {
            // 数据转换
            val contactsMap = mutableMapOf<String, EaseUser>()
            contacts.forEach { contact ->
                contactsMap[contact.hxid.toString()] = EaseUser(contact.hxid)
            }

            // 设置数据
            setContactsMap(contactsMap)

            // 刷新页面
            refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭广播接受器
        mLBM?.unregisterReceiver(contactInviteChangeReceiver)
        mLBM?.unregisterReceiver(contactChangeReceiver)
    }
}