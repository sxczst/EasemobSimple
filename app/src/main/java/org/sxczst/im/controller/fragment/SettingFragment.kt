package org.sxczst.im.controller.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import org.sxczst.im.R
import org.sxczst.im.controller.activity.LoginActivity
import org.sxczst.im.model.Model

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 10:15
 * @Description :设置页面
 */
class SettingFragment : Fragment() {
    private var btnLogout: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = View.inflate(activity, R.layout.fragment_setting, null)
        initView(view)
        return view
    }

    /**
     * 初始化控件
     */
    private fun initView(view: View?) {
        btnLogout = view?.findViewById(R.id.btn_settings_logout)
    }

    /**
     * 当页面创建的时候调用业务逻辑
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    private fun initData() {
        // 在Button上显示当前用户的名称
        btnLogout?.text = "退出登录（${EMClient.getInstance().currentUser}）"
        // 退出登录的逻辑处理
        btnLogout?.setOnClickListener {
            Model.instance.executors.execute {
                // 登录环信服务器退出
                EMClient.getInstance().logout(false, object : EMCallBack {
                    override fun onSuccess() {
                        // 关闭DBHelper
                        Model.instance.dbManager?.close()
                        activity?.runOnUiThread {
                            // 更新UI显示
                            Toast.makeText(activity, "退出成功", Toast.LENGTH_SHORT).show()

                            // 退出回到登录页面
                            startActivity(Intent(activity, LoginActivity::class.java))

                            // 结束当前页面
                            activity?.finish()
                        }
                    }

                    override fun onProgress(p0: Int, p1: String?) {
                    }

                    override fun onError(p0: Int, p1: String?) {
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "退出失败", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
        }
    }
}