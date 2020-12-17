package org.sxczst.im.model.bean

/**
 * @Author      :sxczst
 * @Date        :Created in 2020/10/31 15:28
 * @Description :邀请信息的Bean类
 */
class InvitationInfo(
    var user: UserInfo?,             // 联系人
    var group: GroupInfo?,           // 群组
    var reason: String?,             // 邀请原因
    var inviteStatus: InviteStatus?  //邀请的状态
) {
    constructor() : this(null, null, null, null)

    enum class InviteStatus {
        // 联系人邀请信息状态
        NEW_INVITE,     //新的邀请
        INVITE_ACCEPT,  //接受邀请
        INVITE_ACCEPT_BY_PEER,  //邀请被接受

        // 群组邀请信息状态
        NEW_GROUP_INVITE,       //收到邀请去加入群
        NEW_GROUP_APPLICATION,  //收到申请群加入
        GROUP_INVITE_ACCEPTED,  //群邀请已经被对方接受
        GROUP_APPLICATION_ACCEPTED, //群申请已被批准
        GROUP_INVITE_DECLINED,  //群邀请已经被对方接受
        GROUP_APPLICATION_DECLINED, //群申请已被批准
        GROUP_ACCEPT_INVITE, //群申请已被批准
        GROUP_ACCEPT_APPLICATION, //群申请已被批准
        GROUP_REJECT_INVITE, //群申请已被批准
        GROUP_REJECT_APPLICATION, //群申请已被批准

    }

    override fun toString(): String {
        return "InviteInfo(user=$user, group=$group, reason='$reason', inviteStatus=$inviteStatus)"
    }

}
