package org.sxczst.im.controller.fragment

import android.content.Intent
import android.view.View
import com.hyphenate.easeui.ui.EaseContactListFragment
import org.sxczst.im.R
import org.sxczst.im.controller.activity.AddContactActivity

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 10:15
 * @Description :联系人列表页面
 */
class ContactFragment : EaseContactListFragment() {

    override fun initView() {
        super.initView()

        // 布局显示加号
        titleBar.setRightImageResource(R.drawable.ease_type_select_btn_nor)

        // 添加 headerView
        val headerView = View.inflate(activity, R.layout.header_fragment_contact, null)
        listView.addHeaderView(headerView)
    }

    override fun setUpView() {
        super.setUpView()

        // 添加按钮的点击事件
        titleBar.setRightLayoutClickListener {
            activity?.startActivity(Intent(activity, AddContactActivity::class.java))
        }
    }
}