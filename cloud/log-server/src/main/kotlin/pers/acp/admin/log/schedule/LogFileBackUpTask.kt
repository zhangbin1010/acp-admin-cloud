package pers.acp.admin.log.schedule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.constant.LogBackUp
import pers.acp.core.CalendarTools
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation
import pers.acp.spring.boot.base.BaseSpringBootScheduledAsyncTask
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.cloud.log.LogInstance

import java.io.File

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component("LogFileBackUpTask")
class LogFileBackUpTask @Autowired
constructor(private val logInstance: LogInstance, private val logServerCustomerConfiguration: LogServerCustomerConfiguration) : BaseSpringBootScheduledAsyncTask() {

    @Value("\${server.address}")
    private val serverIp: String? = null

    @Value("\${server.port}")
    private val serverPort: Int = 0

    init {
        taskName = "日志文件备份任务"
    }

    override fun beforeExecuteFun(): Boolean {
        logInstance.info("开始执行日志文件备份")
        return true
    }

    override fun executeFun(): Any? {
        try {
            var day = CalendarTools.getPrevDay(CommonTools.getNowDateTime())
            for (i in 0 until logServerCustomerConfiguration.maxHistoryDayNumber) {
                val logFileDate = CommonTools.getDateTimeString(day, Calculation.DATE_FORMAT)
                val logFold = File(logServerCustomerConfiguration.logFilePath)
                val logFoldPath = logFold.absolutePath
                var zipFilePath = logFoldPath + LogBackUp.BACK_UP_PATH + File.separator + LogBackUp.ZIP_FILE_PREFIX + logFileDate + "_" + serverIp + "_" + serverPort + LogBackUp.EXTENSION
                val zipFile = File(zipFilePath)
                if (!zipFile.exists()) {
                    if (!logFold.exists() || !logFold.isDirectory) {
                        throw ServerException("路径 $logFoldPath 不存在或不是文件夹")
                    }
                    val files = logFold.listFiles { pathname -> pathname.name.contains(logFileDate) }
                    if (files != null && files.isNotEmpty()) {
                        logInstance.info(logFoldPath + " 路径下 " + logFileDate + " 的日志文件（或文件夹）共 " + files.size + " 个")
                        val fileNames = ArrayList<String>()
                        for (file in files) {
                            fileNames.add(file.absolutePath)
                        }
                        logInstance.info("开始执行文件压缩...")
                        zipFilePath = CommonTools.filesToZip(fileNames.toTypedArray(), zipFilePath, true)
                        if (!CommonTools.isNullStr(zipFilePath)) {
                            logInstance.info("文件压缩完成，压缩文件为：$zipFilePath")
                        } else {
                            logInstance.info("文件压缩失败！")
                        }
                    } else {
                        logInstance.debug("$logFoldPath 路径下没有 $logFileDate 的日志文件")
                    }
                }
                day = CalendarTools.getPrevDay(day)
            }
        } catch (e: Exception) {
            logInstance.error(e.message, e)
        }

        return true
    }

    override fun afterExecuteFun(result: Any) {
        try {
            doClearBackUpFiles()
        } catch (e: Exception) {
            logInstance.error(e.message, e)
        }

    }

    private fun doClearBackUpFiles() {
        logInstance.info("开始清理历史备份文件，最大保留天数：" + logServerCustomerConfiguration.maxHistoryDayNumber)
        val filterLogFileNames = ArrayList<String>()
        filterLogFileNames.add("spring.log")
        filterLogFileNames.add(LogBackUp.BACK_UP_PATH.substring(1))
        val filterLogZipFileNames = ArrayList<String>()
        // 保留当天和历史最大天数的文件
        var day = CalendarTools.getPrevDay(CommonTools.getNowDateTime())
        for (i in 0..logServerCustomerConfiguration.maxHistoryDayNumber) {
            filterLogFileNames.add(CommonTools.getDateTimeString(day, Calculation.DATE_FORMAT))
            filterLogZipFileNames.add(LogBackUp.ZIP_FILE_PREFIX + CommonTools.getDateTimeString(day, Calculation.DATE_FORMAT) + "_" + serverIp + "_" + serverPort + LogBackUp.EXTENSION)
            day = CalendarTools.getPrevDay(day)
        }
        // 清理历史日志文件
        val fold = File(logServerCustomerConfiguration.logFilePath)
        doDeleteFileForFold(fold, filterLogFileNames)
        // 清理历史备份压缩日志文件
        val backUpFold = File(logServerCustomerConfiguration.logFilePath + LogBackUp.BACK_UP_PATH)
        doDeleteFileForFold(backUpFold, filterLogZipFileNames)
        logInstance.info("清理历史备份文件完成！")
    }

    private fun doDeleteFileForFold(fold: File, filterNames: List<String>) {
        if (fold.exists()) {
            fold.listFiles { pathname -> !filterNames.contains(pathname.name) }?.let {
                it.forEach { file ->
                    CommonTools.doDeleteFile(file, false)
                }
            }
        }
    }

}