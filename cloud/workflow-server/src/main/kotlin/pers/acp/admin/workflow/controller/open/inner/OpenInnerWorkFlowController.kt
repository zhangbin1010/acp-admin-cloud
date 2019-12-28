package pers.acp.admin.workflow.controller.open.inner

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.common.po.ProcessStartPo
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.api.WorkFlowApi
import pers.acp.admin.workflow.domain.WorkFlowDomain
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
import javax.validation.Valid

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.openInnerBasePath)
@Api(tags = ["工作流引擎（内部开放接口）"])
class OpenInnerWorkFlowController @Autowired
constructor(private val workFlowDomain: WorkFlowDomain) : BaseController() {

    @ApiOperation(value = "启动流程", notes = "启动指定的流程，并关联唯一业务主键")
    @ApiResponses(ApiResponse(code = 201, message = "流程启动成功", response = InfoVo::class), ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PutMapping(value = [WorkFlowApi.start], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun create(@RequestBody @Valid processStartPo: ProcessStartPo): ResponseEntity<InfoVo> =
            workFlowDomain.startFlow(processStartPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(InfoVo(message = it))
            }

}
