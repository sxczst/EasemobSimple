package org.sxczst.im.model.bean

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 15:22
 * @Description :群信息的Bean类
 */
class GroupInfo {
    var groupName: String? = null   // 群名称
    var groupId: String? = null     // 群Id
    var invitePerson: String? = null    // 邀请人

    constructor() {

    }

    constructor(groupName: String?, groupId: String?, invitePerson: String?) {
        this.groupName = groupName
        this.groupId = groupId
        this.invitePerson = invitePerson
    }

    override fun toString(): String {
        return "GroupInfo(groupName=$groupName, groupId=$groupId, invitePerson=$invitePerson)"
    }
}