package org.sxczst.im.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import org.sxczst.im.R
import org.sxczst.im.controller.fragment.ChatFragment
import org.sxczst.im.controller.fragment.ContactFragment
import org.sxczst.im.controller.fragment.SettingFragment

class MainActivity : AppCompatActivity() {
    var rgMain: RadioGroup? = null

    var chatFragment: ChatFragment? = null
    var contactFragment: ContactFragment? = null
    var settingFragment: SettingFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        initData()

        initListener()
    }

    private fun initListener() {
        // RadioGroup 的选择事件
        rgMain?.setOnCheckedChangeListener { group, checkedId ->
            var fragment: Fragment? = null
            when (checkedId) {
                R.id.rb_main_chat -> {
                    // 会话列表页面
                    fragment = chatFragment
                }
                R.id.rb_main_contact -> {
                    // 联系人页面
                    fragment = contactFragment

                }
                R.id.rb_main_setting -> {
                    // 设置页面
                    fragment = settingFragment
                }
            }
            // 实现Fragment 切换的方法
            switchFragment(fragment)
        }

        // 默认选择会话列表页面
        rgMain?.check(R.id.rb_main_chat)
    }

    /**
     * 实现Fragment切换的方法
     */
    private fun switchFragment(fragment: Fragment?) {
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_main, it)
                .commit()
        }
    }

    /**
     * 数据的初始化
     */
    private fun initData() {
        // 创建三个Fragment 对象
        chatFragment = ChatFragment()
        contactFragment = ContactFragment()
        settingFragment = SettingFragment()
    }

    /**
     * 界面的初始化
     */
    private fun initView() {
        rgMain = findViewById(R.id.rg_main)
    }
}