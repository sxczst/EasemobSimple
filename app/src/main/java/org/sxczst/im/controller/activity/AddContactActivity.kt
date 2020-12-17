package org.sxczst.im.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import org.sxczst.im.R
import org.sxczst.im.model.Model
import org.sxczst.im.model.bean.UserInfo

class AddContactActivity : AppCompatActivity() {
    var tvAddFind: TextView? = null
    var etAddName: EditText? = null
    var rlAdd: RelativeLayout? = null
    var tvAddName: TextView? = null
    var btnAddAdd: Button? = null

    // 模拟查到的用户信息
    var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        // 初始化View
        initView()
        // 处理监听事件
        initListener()
    }

    private fun initListener() {
        // 查找按钮的事件
        tvAddFind?.setOnClickListener {
            find()
        }

        // 添加按钮的事件
        btnAddAdd?.setOnClickListener {
            add()
        }
    }

    /**
     * 查找按钮的处理
     */
    private fun find() {
        // 获取输入的用户名称
        val name = etAddName?.text.toString()

        // 校验输入的名称
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "输入的用户名称不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 去服务器判断当前用户是否存在
        Model.instance.executors.execute {
            // 去服务器判断当前用户是否存在 先去自己的服务器
            userInfo = UserInfo(name)   // 模拟自己的服务器

            // 更新UI显示
            runOnUiThread {
                // 布局可见
                rlAdd?.visibility = View.VISIBLE
                tvAddName?.text = userInfo?.name
            }
        }

    }

    /**
     * 添加按钮的处理
     */
    private fun add() {
        // 去环信服务器添加好友 IO 线程中执行
        Model.instance.executors.execute {
            // 去环信服务器添加好友
            try {
                EMClient.getInstance().contactManager().addContact(userInfo?.name, "添加好友")
                runOnUiThread {
                    Toast.makeText(this, "发送添加好友邀请成功", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HyphenateException) {
                runOnUiThread {
                    Toast.makeText(this, "发送添加好友邀请失败$e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initView() {
        tvAddFind = findViewById(R.id.tv_add_find)
        etAddName = findViewById(R.id.et_add_name)
        rlAdd = findViewById(R.id.rl_add)
        tvAddName = findViewById(R.id.tv_add_name)
        btnAddAdd = findViewById(R.id.btn_add_add)
    }

}