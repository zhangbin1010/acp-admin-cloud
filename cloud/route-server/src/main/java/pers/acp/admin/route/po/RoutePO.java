package pers.acp.admin.route.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@ApiModel("网关路由配置参数")
public class RoutePO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPredicates() {
        return predicates;
    }

    public void setPredicates(String predicates) {
        this.predicates = predicates;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "路由ID", required = true, position = 1)
    @NotBlank(message = "路由ID不能为空")
    private String routeId;

    @ApiModelProperty(value = "路由URI", required = true, position = 2)
    @NotBlank(message = "路由URI不能为空")
    private String uri;

    @ApiModelProperty(value = "路由断言", required = true, position = 3)
    @NotBlank(message = "路由断言不能为空")
    private String predicates;

    @ApiModelProperty(value = "路由过滤器", position = 4)
    private String filters;

    @ApiModelProperty(value = "路由序号", position = 5)
    private int orderNum = 0;

    @ApiModelProperty(value = "是否启用", required = true, position = 6)
    private Boolean enabled;

    @ApiModelProperty(value = "备注", position = 7)
    private String remarks;

    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}