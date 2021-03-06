package pers.acp.admin.common.hystrix

import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.common.feign.WorkFlowServer
import pers.acp.admin.common.po.ProcessTerminationPo
import pers.acp.admin.common.po.ProcessHandlingPo
import pers.acp.admin.common.po.ProcessStartPo
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.common.vo.ProcessHistoryActivityVo
import pers.acp.admin.common.vo.ProcessInstanceVo
import pers.acp.admin.common.vo.ProcessTaskVo
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
class WorkFlowServerHystrix
constructor(logAdapter: LogAdapter) : BaseFeignHystrix<WorkFlowServer>(logAdapter) {
    override fun create(cause: Throwable?): WorkFlowServer {
        logAdapter.error("调用 workflow-server 异常: " + cause?.message, cause)
        return object : WorkFlowServer {
            override fun startInner(processStartPo: ProcessStartPo): InfoVo {
                val errMsg = "流程启动失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun terminationInner(processTerminationPo: ProcessTerminationPo): InfoVo {
                val errMsg = "强制终止流程实例失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun startByUser(userId: String, processStartPo: ProcessStartPo): InfoVo {
                val errMsg = "流程启动失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun processByUser(userId: String, processHandlingPo: ProcessHandlingPo): InfoVo {
                val errMsg = "流程处理失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun pendingByUser(processInstanceId: String, userId: String): List<ProcessTaskVo> {
                val errMsg = "任务获取失败"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun start(processStartPo: ProcessStartPo): InfoVo {
                val errMsg = "流程启动失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun claim(taskId: String): InfoVo {
                val errMsg = "任务领取失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun transfer(taskId: String, userId: String): InfoVo {
                val errMsg = "任务转办失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun delegate(taskId: String, acceptUserId: String): InfoVo {
                val errMsg = "任务委托办理失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun process(processHandlingPo: ProcessHandlingPo): InfoVo {
                val errMsg = "任务处理失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun termination(processTerminationPo: ProcessTerminationPo): InfoVo {
                val errMsg = "强制终止流程实例失败"
                logAdapter.error(errMsg)
                return InfoVo()
            }

            override fun getInstance(processInstanceId: String): ProcessInstanceVo? {
                val errMsg = "获取流程实例失败"
                logAdapter.error(errMsg)
                return null
            }

            override fun getTaskInfoOpen(taskId: String): ProcessTaskVo? {
                val errMsg = "获取任务信息失败"
                logAdapter.error(errMsg)
                return null
            }

            override fun getHistoryActivity(processInstanceId: String): List<ProcessHistoryActivityVo> {
                val errMsg = "获取流程处理记录失败"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun getTaskInfo(taskId: String): ProcessTaskVo? {
                val errMsg = "获取任务信息失败"
                logAdapter.error(errMsg)
                return null
            }

        }
    }
}