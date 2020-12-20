package org.sxczst.im.controller.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.activity_pick_contact.*
import org.sxczst.im.R
import org.sxczst.im.controller.adapter.PickContactAdapter
import org.sxczst.im.model.Model
import org.sxczst.im.model.bean.PickContactInfo
import org.sxczst.im.utils.Constants

/**
 * 选择联系人页面
 */
class PickContactActivity : AppCompatActivity() {
    /**
     * 要展示的联系人数据信息
     */
    private lateinit var pickContacts: MutableList<PickContactInfo>

    /**
     * 选择联系人适配器
     */
    private lateinit var adapter: PickContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_contact)

        // 初始化布局
        initView()

        // 初始化数据
        initData()

        // 初始化监听
        initListener()
    }

    /**
     * 初始化监听
     */
    private fun initListener() {
        // ListView 的条目点击事件
        lv_pick.setOnItemClickListener { parent, view, position, id ->
            // 联系人选择框
            val checkBoxPick = view.findViewById<CheckBox>(R.id.cb_pick)
            // 取反
            checkBoxPick.isChecked = !checkBoxPick.isChecked

            // 修改数据
            pickContacts[position].isChecked = checkBoxPick.isChecked

            // 刷新页面
            adapter.notifyDataSetChanged()
        }

        // 保存按钮的点击事件
        btn_pick_save.setOnClickListener {
            // 获取已经选择的联系人名称(环信ID)信息
            val picks = adapter.getPickContacts()
            val intent = Intent()
            intent.putExtra("members", picks.toTypedArray())
            // 设置返回的结果码和联系人信息
            setResult(Activity.RESULT_OK, intent)
            // 结束当前页面
            finish()
        }
    }

    /**
     * 初始化布局
     */
    private fun initView() {
        // 控件对象主要交由 Kotlin 特性获取。
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        // 从本地数据库中获取所有的联系人信息
        val contacts = Model.instance.dbManager?.contactDao?.getContacts()
        pickContacts = mutableListOf<PickContactInfo>()
        contacts?.let {
            // 数据转换
            it.forEach { contact ->
                pickContacts.add(PickContactInfo(contact, false))
            }
        }
        adapter = PickContactAdapter(this, pickContacts)
        lv_pick.adapter = adapter

        // 获取传递过来的数据(组Id)
        getData()
    }

    /**
     * 获取传递过来的数据(组Id)
     */
    private fun getData() {
        val groupId = intent.getStringExtra(Constants.GROUP_ID)
        groupId?.let {
            val group = EMClient.getInstance().groupManager().getGroup(it)
            // 获取群组中已经存在的成员
            adapter.setGroupMembers(group.members)
        }
    }
}