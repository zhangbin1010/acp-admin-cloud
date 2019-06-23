package pers.acp.admin.log.controller;

import io.swagger.annotations.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.log.constant.LogApi;
import pers.acp.admin.log.constant.LogFileExpression;
import pers.acp.admin.log.domain.LogFileDomain;
import pers.acp.core.CommonTools;
import pers.acp.core.task.timer.container.Calculation;
import pers.acp.spring.boot.exceptions.ServerException;
import pers.acp.spring.boot.vo.ErrorVO;
import pers.acp.spring.cloud.log.LogInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(LogApi.basePath)
@Api("日志信息")
public class LogController extends BaseController {

    private final LogInstance logInstance;

    private final LogFileDomain logFileDomain;

    @Autowired
    public LogController(LogInstance logInstance, LogFileDomain logFileDomain) {
        this.logInstance = logInstance;
        this.logFileDomain = logFileDomain;
    }

    @ApiOperation(value = "查询指定日期范围的日志备份文件",
            notes = "查询条件：开始日期、结束日期")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(LogFileExpression.adminOnly)
    @PostMapping(value = LogApi.logFile, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> query(@ApiParam(value = "开始日期", required = true, example = "0") @NotNull(message = "开始日期不能为空")
                                              @RequestParam long startDate,
                                              @ApiParam(value = "结束日期", required = true, example = "0") @NotNull(message = "结束日期不能为空")
                                              @RequestParam long endDate) throws ServerException {
        try {
            DateTime start = longToDate(startDate);
            DateTime end = longToDate(endDate);
            DateTime nowDay = longToDate(CommonTools.getNowDateTime().toDate().getTime());
            if (start.compareTo(end) > 0) {
                throw new ServerException("开始日期不能大于结束日期");
            }
            if (end.compareTo(nowDay) >= 0) {
                throw new ServerException("结束日期必须早于当前");
            }
            return ResponseEntity.ok(logFileDomain.fileList(CommonTools.getDateTimeString(start, Calculation.DATE_FORMAT),
                    CommonTools.getDateTimeString(end, Calculation.DATE_FORMAT)));
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    @ApiOperation(value = "日志文件下载",
            notes = "下载指定的日志文件")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(LogFileExpression.adminOnly)
    @GetMapping(value = LogApi.logFile + "/{fileName}", produces = MediaType.ALL_VALUE)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @ApiParam(value = "文件名称", required = true) @NotBlank(message = "文件名称不能为空")
                         @PathVariable String fileName) throws ServerException {
        logFileDomain.doDownLoadFile(request, response, fileName);
    }

}
