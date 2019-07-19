package pers.acp.admin.oauth.repo

import pers.acp.admin.oauth.base.OauthBaseRepository
import pers.acp.admin.oauth.entity.Organization

/**
 * @author zhangbin by 2018-1-17 17:45
 * @since JDK 11
 */
interface OrganizationRepository : OauthBaseRepository<Organization, String> {

    fun findByParentIdIn(idList: MutableList<String>): MutableList<Organization>

    fun deleteByIdIn(idList: MutableList<String>)

}