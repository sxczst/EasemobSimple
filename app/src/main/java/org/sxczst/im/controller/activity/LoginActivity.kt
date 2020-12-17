package org.sxczst.im.controller.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import kotlinx.android.synthetic.main.activity_login.*
import org.sxczst.im.R
import org.sxczst.im.model.Model
import org.sxczst.im.model.bean.UserInfo

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        //注册按钮的点击事件处理
        btn_login_register.setOnClickListener {
            register()
        }

        btn_login_login.setOnClickListener {
            login()
        }
    }


    /**
     * 登录的业务逻辑处理
     */
    private fun login() {
        // 1)获取用户名和密码
        val loginName = et_login_name.text.toString()
        val loginPWD = et_login_pwd.text.toString()

        // 2)校验输入的用户名和密码
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPWD)) {
            Toast.makeText(this, "输入的用户名或密码为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 3)登录逻辑处理
        Model.instance.executors.execute {
            EMClient.getInstance().login(loginName, loginPWD, object : EMCallBack {
                /**
                 * 登录成功后的处理
                 */
                override fun onSuccess() {
                    // 对模型层数据的处理
                    Model.instance.loginSuccess(UserInfo(loginName))

                    // 保存用户账号信息至本地数据库
                    Model.instance.userAccountDao?.addAccount(UserInfo(loginName))

                    // Toast 提示登录成功 切换至主线程操作
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()

                        // 跳转到主页面
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                        finish()
                    }

                }

                /**
                 * 登录过程中的处理
                 */
                override fun onProgress(p0: Int, p1: String?) {
                }

                /**
                 * 登录失败的处理
                 */
                override fun onError(p0: Int, p1: String?) {
                    // Toast 提示登录失败 切换至主线程操作
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "登录失败$p1", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    /**
     * 注册的业务逻辑处理
     */
    private fun register() {
        // 1)获取用户名和密码
        val registerName = et_login_name.text.toString()
        val registerPWD = et_login_pwd.text.toString()

        // 2)校验输入的用户名和密码
        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPWD)) {
            Toast.makeText(this, "输入的用户名或密码为空", Toast.LENGTH_SHORT).show()
            return
        }

        // 3)去服务器注册账号
        Model.instance.executors.execute {
            // 去环信服务器注册账号
            try {
                EMClient.getInstance().createAccount(registerName, registerPWD)

                // 更新页面显示
                runOnUiThread {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HyphenateException) {
                // 更新页面显示
                runOnUiThread {
                    Toast.makeText(this, "注册失败$e", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
}