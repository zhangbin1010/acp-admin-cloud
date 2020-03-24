package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.ModuleFunc
import pers.acp.admin.oauth.po.ModuleFuncPo
import pers.acp.admin.oauth.repo.ModuleFuncRepository
import pers.acp.admin.oauth.repo.RoleRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.vo.ModuleFuncVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class ModuleFuncDomain @Autowired
constructor(userRepository: UserRepository,
            private val roleRepository: RoleRepository,
            private val moduleFuncRepository: ModuleFuncRepository) : OauthBaseDomain(userRepository) {

    fun getAllModuleFuncList(): MutableList<ModuleFunc> =
            moduleFuncRepository.findAll().let {
                val map: MutableMap<String, ModuleFunc> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortModuleFuncList(formatToTreeList(map))
            }

    fun getModuleFuncList(appId: String, loginNo: String): MutableList<ModuleFunc> =
            (findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")).let {
                val moduleFuncIds: MutableSet<String> = mutableSetOf()
                it.roleSet.filter { role -> role.appId == appId }
                        .flatMap { item -> item.moduleFuncSet }
                        .filter { item ->
                            if (moduleFuncIds.contains(item.id)) {
                                false
                            } else {
                                moduleFuncIds.add(item.id)
                                true
                            }
                        }
                        .filter { menu -> menu.appId == appId }
                        .toMutableList()
            }

    fun hasModuleFunc(appId: String, loginNo: String, moduleFuncCode: String): Boolean =
            getModuleFuncList(appId, loginNo).any { item -> item.code == moduleFuncCode }

    private fun sortModuleFuncList(moduleFuncList: MutableList<ModuleFunc>): MutableList<ModuleFunc> =
            moduleFuncList.let { list ->
                list.forEach { organization ->
                    if (organization.children.isNotEmpty()) {
                        sortModuleFuncList(organization.children)
                    }
                }
                moduleFuncList.apply {
                    this.sortBy { it.code }
                }
            }

    fun getModuleFuncListByAppId(appId: String): List<ModuleFunc> =
            moduleFuncRepository.findByAppId(appId).let {
                val map: MutableMap<String, ModuleFunc> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortModuleFuncList(formatToTreeList(map))
            }

    private fun doSave(moduleFunc: ModuleFunc, moduleFuncPo: ModuleFuncPo): ModuleFunc =
            moduleFuncRepository.save(moduleFunc.copy(
                    name = moduleFuncPo.name!!,
                    code = moduleFuncPo.code!!,
                    roleSet = roleRepository.findAllById(moduleFuncPo.roleIds).toMutableSet()
            ).apply {
                parentId = moduleFuncPo.parentId!!
            })

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(moduleFuncPo: ModuleFuncPo): ModuleFunc =
            moduleFuncRepository.findByCode(moduleFuncPo.code!!).let {
                if (it.isPresent) {
                    throw ServerException("编码重复")
                }
                doSave(ModuleFunc(
                        appId = moduleFuncPo.appId!!,
                        covert = true
                ), moduleFuncPo)
            }

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(idList: MutableList<String>) {
        moduleFuncRepository.findByParentIdIn(idList).apply {
            if (this.isNotEmpty()) {
                throw ServerException("存在下级模块功能，不允许删除")
            }
        }
        moduleFuncRepository.deleteByIdInAndCovert(idList, true)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(moduleFuncPo: ModuleFuncPo): ModuleFunc =
            moduleFuncRepository.findByCodeAndIdNot(moduleFuncPo.code!!, moduleFuncPo.id!!).let {
                if (it.isPresent) {
                    throw ServerException("编码重复")
                }
                doSave(moduleFuncRepository.getOne(moduleFuncPo.id!!), moduleFuncPo)
            }

    @Throws(ServerException::class)
    fun getModuleFuncInfo(moduleFuncId: String): ModuleFuncVo =
            moduleFuncRepository.getOne(moduleFuncId).let { item ->
                ModuleFuncVo(
                        id = item.id,
                        appId = item.appId,
                        code = item.code,
                        name = item.name,
                        parentId = item.parentId,
                        roleIds = item.roleSet.map { it.id }.toMutableList()
                )
            }

}
