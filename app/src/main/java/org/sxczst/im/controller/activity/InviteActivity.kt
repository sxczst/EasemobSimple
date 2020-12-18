package org.sxczst.im.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_invite.*
import org.sxczst.im.R
import org.sxczst.im.controller.adapter.InviteAdapter

/**
 * 邀请信息列表页面
 */
class InviteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        initData()
    }

    private fun initData() {
        // 初始化ListView
        val adapter = InviteAdapter(this)
        lv_invite.adapter = adapter
    }
}