package org.sxczst.im.controller.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.hyphenate.chat.EMClient
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.exceptions.HyphenateException
import kotlinx.android.synthetic.main.activity_group_list.*
import org.sxczst.im.R
import org.sxczst.im.controller.adapter.GroupListAdapter
import org.sxczst.im.model.Model

class GroupListActivity : AppCompatActivity() {
    /**
     * 添加群组按钮
     */
    private lateinit var constraintLayout: ConstraintLayout

    /**
     * 群组列表适配器
     */
    private lateinit var adapter: GroupListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)
        // 初始化布局信息
        initView()

        // 获取群组数据
        initData()

        // 添加监听事件
        initListener()
    }

    override fun onResume() {
        super.onResume()
        // 刷新页面
        refresh()
    }

    /**
     * 初始化布局信息
     */
    private fun initView() {
        val headerView = LayoutInflater.from(this)
            .inflate(R.layout.header_group_list, null)
        lv_group_list.addHeaderView(headerView)

        // 添加群组的
        constraintLayout = headerView.findViewById<ConstraintLayout>(R.id.cl_group_list_header)
    }

    /**
     * 获取群组数据
     */
    private fun initData() {
        adapter = GroupListAdapter(this)
        lv_group_list.adapter = adapter

        // 从环信服务器获取所有的群组信息
        getGroupsFromServer()
    }

    private fun initListener() {
        // ListView的条目点击监听
        lv_group_list.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                return@setOnItemClickListener
            }
            val intent = Intent(this, ChatActivity::class.java)
            // 传递会话类型(群组类型)
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
            val emGroup = EMClient.getInstance().groupManager().allGroups[position - 1]
            // 群ID
            intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.groupId)
            // 执行跳转
            startActivity(intent)
        }

        // 跳转到新建群组界面
        constraintLayout.setOnClickListener {
            startActivity(Intent(this@GroupListActivity, NewGroupActivity::class.java))
        }
    }

    /**
     * 从环信服务器获取所有的群组信息
     */
    private fun getGroupsFromServer() {
        Model.instance.executors.execute {
            try {
                // 从网络获取数据
                val mGroups =
                    EMClient.getInstance().groupManager().joinedGroupsFromServer

                runOnUiThread {
                    Toast.makeText(this, "加载群信息成功", Toast.LENGTH_SHORT).show()
                    // 刷新显示
                    refresh()
                }
            } catch (e: HyphenateException) {
                runOnUiThread {
                    Toast.makeText(this, "加载群信息失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 刷新列表显示的数据
     */
    private fun refresh() {
        adapter.refresh(EMClient.getInstance().groupManager().allGroups)
    }
}