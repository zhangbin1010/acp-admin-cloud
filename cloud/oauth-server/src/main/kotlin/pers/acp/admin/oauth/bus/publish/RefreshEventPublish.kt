package pers.acp.admin.oauth.bus.publish

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.bus.BusProperties
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import pers.acp.admin.common.event.ReloadDataBusEvent
import pers.acp.admin.oauth.constant.BusEventMessage

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
class RefreshEventPublish @Autowired
constructor(private val applicationContext: ApplicationContext, private val busProperties: BusProperties) {

    fun doNotifyUpdateApp() {
        val source = this
        applicationContext.publishEvent(
            ReloadDataBusEvent(
                busProperties.id,
                null,
                BusEventMessage.refreshApplication,
                source
            )
        )
    }

    fun doNotifyUpdateRuntime() {
        applicationContext.publishEvent(
            ReloadDataBusEvent(
                busProperties.id,
                null,
                BusEventMessage.refreshRuntime,
                this
            )
        )
    }

}
