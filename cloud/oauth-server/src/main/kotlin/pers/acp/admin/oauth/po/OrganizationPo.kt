package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 17/01/2019
 * @since JDK 11
 */
@ApiModel("机构配置参数")
data class OrganizationPo(

        @ApiModelProperty("机构ID，更新时必填")
        var id: String? = null,

        @ApiModelProperty(value = "机构名称", required = true, position = 1)
        @get:NotBlank(message = "机构名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "机构编码", position = 2)
        var code: String = "",

        @ApiModelProperty(value = "上级机构ID", required = true, position = 3)
        @get:NotBlank(message = "上级机构ID不能为空")
        var parentId: String? = null,

        @ApiModelProperty(value = "序号", required = true, position = 4)
        var sort: Int = 0,

        @ApiModelProperty(value = "关联用户ID", position = 5)
        var userIds: MutableList<String> = mutableListOf()

)
