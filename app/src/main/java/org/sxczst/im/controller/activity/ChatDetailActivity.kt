package org.sxczst.im.controller.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMGroup
import com.hyphenate.exceptions.HyphenateException
import kotlinx.android.synthetic.main.activity_chat_detail.*
import org.sxczst.im.R
import org.sxczst.im.controller.adapter.GroupDetailAdapter
import org.sxczst.im.model.Model
import org.sxczst.im.model.bean.UserInfo
import org.sxczst.im.utils.Constants

/**
 * 群详情界面
 */
class ChatDetailActivity : AppCompatActivity() {
    /**
     * 群聊详情列表条目适配器
     */
    private lateinit var adapter: GroupDetailAdapter

    /**
     * 群成员的添加和删除回调监听
     */
    private val mOnGroupDetailListener = object : GroupDetailAdapter.OnGroupDetailListener {
        override fun onAddMembers() {

        }

        /**
         * 删除群成员的方法
         */
        override fun onDeleteMember(userInfo: UserInfo) {
            Model.instance.executors.execute {
                try {
                    // 去环信服务器删除该成员信息
                    EMClient.getInstance().groupManager().removeUserFromGroup(
                        mGroup?.groupId,
                        userInfo.hxid
                    )
                    // 更新页面
                    getMembersFromHxServer()
                    runOnUiThread {
                        Toast.makeText(this@ChatDetailActivity, "删除成功", Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: HyphenateException) {
                    // 更新页面
                    runOnUiThread {
                        Toast.makeText(this@ChatDetailActivity, "删除失败$e", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    /**
     * 是否可以添加或删除成员
     */
    private var isCanModify: Boolean = false

    /**
     * 根据传递过来的群组Id从环信中获取到群组信息
     */
    private var mGroup: EMGroup? = null

    /**
     * 获取本地广播管理者
     */
    private val mLBM: LocalBroadcastManager = LocalBroadcastManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)
        initView()
        initData()
        initListener()
    }

    /**
     * 初始化监听
     */
    private fun initListener() {
        // 初始化button的提示信息
        initButtonDisplay()
    }

    /**
     * 初始化GridView的显示
     */
    private fun initGridViewDisplay() {
        // 当前用户是群主或者群聊是公开的就可以修改群成员了
        mGroup?.isPublic?.let {
            isCanModify = EMClient.getInstance().currentUser == mGroup?.owner || it
        }

        adapter =
            GroupDetailAdapter(this@ChatDetailActivity, isCanModify, mOnGroupDetailListener)

        gv_group_detail.adapter = adapter
    }

    /**
     * 初始化button的提示信息
     */
    private fun initButtonDisplay() {
        // 判断当前用户是否是群主
        if (EMClient.getInstance().currentUser == mGroup?.owner) {
            // 群主
            btn_group_detail_out.text = "解散群聊"
            btn_group_detail_out.setOnClickListener {
                Model.instance.executors.execute {
                    try {
                        // 去环信服务器解散该群聊
                        EMClient.getInstance().groupManager().destroyGroup(mGroup?.groupId)

                        // 发送群解散的广播
                        exitGroupBroadcast()

                        // 更新页面
                        runOnUiThread {
                            Toast.makeText(this@ChatDetailActivity, "解散群聊成功", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    } catch (e: HyphenateException) {
                        // 更新页面
                        runOnUiThread {
                            Toast.makeText(this@ChatDetailActivity, "解散群聊失败", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        } else {
            // 群成员
            btn_group_detail_out.text = "退出群聊"
            btn_group_detail_out.setOnClickListener {
                Model.instance.executors.execute {
                    try {
                        // 告诉环信服务器退出群聊
                        EMClient.getInstance().groupManager().leaveGroup(mGroup?.groupId)

                        // 发送群解散的广播
                        exitGroupBroadcast()

                        // 更新页面
                        runOnUiThread {
                            Toast.makeText(this@ChatDetailActivity, "退出群聊成功", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    } catch (e: HyphenateException) {
                        // 更新页面
                        runOnUiThread {
                            Toast.makeText(
                                this@ChatDetailActivity,
                                "退出群聊失败$e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }

    /**
     * 退出群组的广播
     */
    private fun exitGroupBroadcast() {
        val intent = Intent(Constants.EXIT_GROUP)
        // 携带参数
        intent.putExtra(Constants.GROUP_ID, mGroup?.groupId)
        mLBM.sendBroadcast(intent)
    }

    /**
     * 初始化控件
     */
    private fun initView() {
        // 控件的实例化主要交由 Kotlin 的特性实现。
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        // 获取传递过来的数据
        val groupId = intent.getStringExtra(Constants.GROUP_ID)
        groupId?.let {
            mGroup = EMClient.getInstance().groupManager().getGroup(it)
        }

        // 初始化GridView的显示
        initGridViewDisplay()

        // 从环信服务器中获取所有的群成员信息
        getMembersFromHxServer()
    }

    /**
     * 从环信服务器中获取所有的群成员信息
     */
    private fun getMembersFromHxServer() {
        Model.instance.executors.execute {
            try {
                // 从环信服务器中获取所有的群成员信息
                val emGroup =
                    EMClient.getInstance().groupManager().getGroupFromServer(mGroup?.groupId)
                val mUsers = mutableListOf<UserInfo>()
                // 得到所有群成员的环信ID
                emGroup.members.forEach { member ->
                    // 转换
                    mUsers.add(UserInfo(member))
                }
                // 刷新页面
                runOnUiThread {
                    // 刷新适配器
                    adapter.refresh(mUsers)
                }
            } catch (e: HyphenateException) {
                Toast.makeText(this@ChatDetailActivity, "获取群成员信息失败$e", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
}