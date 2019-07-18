package pers.acp.admin.route.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_gateway_route")
@ApiModel("网关路由配置")
data class Route(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String? = null,

        @ApiModelProperty("路由ID")
        @Column(nullable = false)
        var routeId: String? = null,

        @ApiModelProperty("路由URI")
        @Column(nullable = false)
        var uri: String? = null,

        @ApiModelProperty("路由断言")
        @Column(columnDefinition = "text", nullable = false)
        var predicates: String? = null,

        @ApiModelProperty("路由过滤器")
        @Column(columnDefinition = "text")
        var filters: String? = null,

        @ApiModelProperty("路由序号")
        var orderNum: Int = 0,

        @ApiModelProperty("是否启用")
        var enabled: Boolean = false,

        @ApiModelProperty("备注")
        var remarks: String? = null

)
