package org.sxczst.im.controller.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.sxczst.im.R
import org.sxczst.im.model.bean.UserInfo

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/12/20 10:55
 * @Description :群聊详情列表条目适配器
 */
class GroupDetailAdapter(
    var context: Context,
    var isCanModify: Boolean,
    var onGroupDetailListener: OnGroupDetailListener
) : BaseAdapter() {
    private var mUsers = mutableListOf<UserInfo>()

    /**
     * 删除模式
     * true ：可以进行删除操作
     * false：不可以进行删除操作
     */
    private var mIsDeleteModel = false

    /**
     * 刷新要显示的数据
     */
    fun refresh(users: List<UserInfo>) {
        mUsers.clear()
        mUsers.addAll(users)
        mUsers.add(UserInfo("add"))
        mUsers.add(UserInfo("delete"))
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder? = null
        var view: View? = null
        // 获取或者创建ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_group_detail, null)
            viewHolder = ViewHolder(
                view?.findViewById(R.id.iv_group_detail_photo),
                view?.findViewById(R.id.tv_group_detail_name),
                view?.findViewById(R.id.iv_group_detail_delete)
            )
            view.tag = viewHolder
        }
        convertView?.tag?.let {
            viewHolder = it as ViewHolder
        }

        // 获取当前Item的数据
        val userInfo = mUsers[position]

        // 展示数据
        if (isCanModify) {
            // 当前是群主在在操作或者是开放了群的权限

            // 只处理布局显示
            when (position) {
                count - 1 -> {
                    // 减号的处理
                    if (mIsDeleteModel) {
                        // 删除模式
                        convertView?.visibility = View.INVISIBLE
                    } else {
                        convertView?.visibility = View.VISIBLE
                        viewHolder?.photo?.setImageResource(R.drawable.ease_search_clear)
                        viewHolder?.delete?.visibility = View.GONE
                        viewHolder?.name?.text = ""
                    }
                }
                count - 2 -> {
                    // 加号的处理
                    if (mIsDeleteModel) {
                        // 删除模式
                        convertView?.visibility = View.INVISIBLE
                    } else {
                        convertView?.visibility = View.VISIBLE
                        viewHolder?.photo?.setImageResource(R.drawable.ease_type_select_btn)
                        viewHolder?.delete?.visibility = View.GONE
                        viewHolder?.name?.text = ""
                    }
                }
                else -> {
                    // 其他的处理
                    convertView?.visibility = View.VISIBLE
                    viewHolder?.name?.text = userInfo.name
                    viewHolder?.delete?.visibility = if (mIsDeleteModel) View.VISIBLE else View.GONE
                }
            }

            // 只处理交互逻辑
            when (position) {
                count - 1 -> {
                    // 减号的处理
                    viewHolder?.photo?.setOnClickListener {
                        if (!mIsDeleteModel) {
                            mIsDeleteModel = true
                            notifyDataSetChanged()
                        }
                    }
                }
                count - 2 -> {
                    // 加号的处理
                    viewHolder?.photo?.setOnClickListener {
                        onGroupDetailListener.onAddMembers()
                    }
                }
                else -> {
                    // 其他的处理
                    viewHolder?.delete?.setOnClickListener {
                        onGroupDetailListener.onDeleteMember(userInfo)
                    }
                }
            }

        } else {
            // 不可修改
            if (position == count - 1 || position == count - 2) {
                // 添加和删除操作
                convertView?.visibility = View.GONE
            } else {
                convertView?.visibility = View.VISIBLE
                // 显示名称
                viewHolder?.name?.text = userInfo.name
                // 隐藏删除成员按钮
                viewHolder?.delete?.visibility = View.GONE
            }
        }
        // 返回view
        return view ?: convertView ?: View(context)
    }

    override fun getItem(position: Int): Any = mUsers[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mUsers.size

    inner class ViewHolder(var photo: ImageView?, var name: TextView?, var delete: ImageView?)

    interface OnGroupDetailListener {
        /**
         * 添加群成员的方法
         */
        fun onAddMembers()

        /**
         * 删除群成员的方法
         */
        fun onDeleteMember(userInfo: UserInfo)
    }
}