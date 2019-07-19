package pers.acp.admin.config.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.config.entity.Properties
import pers.acp.admin.config.po.PropertiesPo
import pers.acp.admin.config.repo.PropertiesRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException

import javax.persistence.criteria.Predicate

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class PropertiesDomain @Autowired
constructor(private val propertiesRepository: PropertiesRepository) : BaseDomain() {

    /**
     * 检查相同服务名、配置项、标签、键且已启用的服务配置是否已存在
     *
     * @param propertiesPo 参数
     * @throws ServerException 异常信息
     */
    @Throws(ServerException::class)
    private fun checkEnabledExist(propertiesPo: PropertiesPo) =
            propertiesPo.enabled?.let {
                if (it) {
                    propertiesRepository.findByConfigApplicationAndConfigProfileAndConfigLabelAndConfigKeyAndEnabledAndIdNot(
                            propertiesPo.configApplication!!,
                            propertiesPo.configProfile!!,
                            propertiesPo.configLabel!!,
                            propertiesPo.configKey!!,
                            propertiesPo.enabled!!,
                            propertiesPo.id!!).ifPresent {
                        throw ServerException("有效的服务配置已存在，请重新提交或改为\"不可用\"")
                    }
                }
            }

    private fun doSave(properties: Properties, propertiesPo: PropertiesPo): Properties =
            properties.apply {
                configApplication = propertiesPo.configApplication!!
                configProfile = propertiesPo.configProfile!!
                configLabel = propertiesPo.configLabel!!
                configKey = propertiesPo.configKey!!
                configValue = propertiesPo.configValue!!
                configDes = propertiesPo.configDes
                enabled = propertiesPo.enabled ?: false
            }.let {
                propertiesRepository.save(it)
            }

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(propertiesPo: PropertiesPo): Properties =
            propertiesPo.apply {
                this.id = ""
            }.let {
                checkEnabledExist(it)
                doSave(Properties(), it)
            }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(propertiesPo: PropertiesPo): Properties =
            propertiesRepository.getOne(propertiesPo.id!!).apply {
                checkEnabledExist(propertiesPo)
            }.let {
                doSave(it, propertiesPo)
            }

    @Transactional
    fun doDelete(idList: List<String>) = propertiesRepository.deleteByIdInAndEnabled(idList, false)

    fun doQuery(propertiesPo: PropertiesPo): Page<Properties> =
            propertiesRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (propertiesPo.enabled != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("enabled"), propertiesPo.enabled))
                }
                if (!CommonTools.isNullStr(propertiesPo.configApplication)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("configApplication").`as`(String::class.java), "%" + propertiesPo.configApplication + "%"))
                }
                if (!CommonTools.isNullStr(propertiesPo.configProfile)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("configProfile").`as`(String::class.java), "%" + propertiesPo.configProfile + "%"))
                }
                if (!CommonTools.isNullStr(propertiesPo.configLabel)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("configLabel").`as`(String::class.java), "%" + propertiesPo.configLabel + "%"))
                }
                if (!CommonTools.isNullStr(propertiesPo.configKey)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("configKey").`as`(String::class.java), "%" + propertiesPo.configKey + "%"))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(propertiesPo.queryParam!!))

}