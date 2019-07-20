package pers.acp.admin.oauth.constant

import pers.acp.admin.common.constant.ModuleFuncCode
import pers.acp.admin.common.constant.RoleCode
import pers.acp.admin.common.permission.BaseExpression

/**
 * 定义用户配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object UserConfigExpression : BaseExpression() {
    const val adminOnly = BaseExpression.adminOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 用户配置
     */
    const val userConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userConfig + "')"

    /**
     * 用户新增
     */
    const val userAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userAdd + "')"

    /**
     * 用户删除
     */
    const val userDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userDelete + "')"

    /**
     * 用户更新
     */
    const val userUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userUpdate + "')"

    /**
     * 用户查询
     */
    const val userQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userQuery + "')"
}