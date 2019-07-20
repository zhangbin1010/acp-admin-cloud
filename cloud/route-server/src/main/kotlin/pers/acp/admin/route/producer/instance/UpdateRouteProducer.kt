package pers.acp.admin.route.producer.instance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import pers.acp.admin.route.constant.GateWayConstant
import pers.acp.admin.route.producer.UpdateRouteOutput

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class UpdateRouteProducer @Autowired
constructor(private val updateRouteOutput: UpdateRouteOutput) {

    fun doNotifyUpdateRoute() {
        updateRouteOutput.sendMessage().send(MessageBuilder.withPayload(GateWayConstant.UPDATE_GATEWAY_ROUTES).build())
    }

}
