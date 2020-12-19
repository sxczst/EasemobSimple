package org.sxczst.im.controller.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMGroupManager
import com.hyphenate.chat.EMGroupManager.EMGroupStyle.*
import com.hyphenate.chat.EMGroupOptions
import com.hyphenate.exceptions.HyphenateException
import kotlinx.android.synthetic.main.activity_new_group.*
import org.sxczst.im.R
import org.sxczst.im.model.Model

/**
 * 创建新的群组页面
 */
class NewGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        // 初始化布局
        initView()

        // 初始化监听
        initListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // 成功获取到回传的联系人信息
            createGroup(data?.getStringArrayExtra("members"))
        }
    }

    /**
     * 创建一个新的群组
     */
    private fun createGroup(hxids: Array<String>?) {
        // 群名称
        val groupName = et_new_group_name.text.toString()
        // 群描述
        val groupDESC = et_new_group_desc.text.toString()
        // 群组参数配置
        val groupStyle: EMGroupManager.EMGroupStyle
        if (cb_new_group_public.isChecked) {
            // 公开
            groupStyle = if (cb_new_group_invite.isChecked) {
                // 允许邀请
                EMGroupStylePublicOpenJoin
            } else {
                // 不允许邀请
                EMGroupStylePublicJoinNeedApproval
            }
        } else {
            // 不公开
            groupStyle = if (cb_new_group_invite.isChecked) {
                // 允许邀请
                EMGroupStylePrivateMemberCanInvite
            } else {
                // 不允许邀请
                EMGroupStylePublicJoinNeedApproval
            }
        }

        // 去环信服务器创建群组
        Model.instance.executors.execute {
            // 参数配置
            val options = EMGroupOptions()
            options.maxUsers = 200
            // 配置群的类型
            options.style = groupStyle

            try {// 参数一：群名称，参数二：群描述，参数三：群成员，参数四：原因，参数五，参数配置。
                EMClient.getInstance().groupManager().createGroup(
                    groupName,
                    groupDESC,
                    hxids,
                    "申请加入群聊",
                    options
                )
                runOnUiThread {
                    Toast.makeText(this@NewGroupActivity, "创建群组成功", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HyphenateException) {
                runOnUiThread {
                    Toast.makeText(this@NewGroupActivity, "创建群组失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    /**
     * 初始化监听
     */
    private fun initListener() {
        // 创建按钮的点击处理事件
        btn_new_group_create.setOnClickListener {
            // 跳转到选择联系人页面
            Intent(this@NewGroupActivity, PickContactActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    /**
     * 初始化布局
     */
    private fun initView() {
        // 控件对象主要交由 Kotlin 特性获取。
    }
}