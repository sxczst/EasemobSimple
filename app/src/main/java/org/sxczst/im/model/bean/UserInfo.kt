package org.sxczst.im.model.bean

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 7:56
 * @Description :用户账号信息的Bean类
 */
class UserInfo {
    var name: String? = null    // 用户名称
    var hxid: String? = null    // 环信ID
    var nick: String? = null    // 用户昵称
    var photo: String? = null   // 头像

    constructor() {}
    constructor(name: String) {
        this.name = name
        this.hxid = name
        this.nick = name
    }

    override fun toString(): String {
        return "UserInfo(name=$name, hxid=$hxid, nick=$nick, photo=$photo)"
    }
}