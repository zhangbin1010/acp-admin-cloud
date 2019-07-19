package pers.acp.admin.gateway.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.util.MimeTypeUtils
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import pers.acp.admin.gateway.constant.GateWayConstant
import pers.acp.admin.gateway.consumer.UpdateRouteInput
import pers.acp.admin.gateway.consumer.instance.UpdateRouteConsumer
import pers.acp.admin.gateway.domain.RouteLogDomain
import pers.acp.admin.gateway.producer.RouteLogOutput
import pers.acp.admin.gateway.repo.RouteRedisRepository
import reactor.core.publisher.Mono

import javax.annotation.PostConstruct

/**
 * @author zhang by 17/12/2018 00:41
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(BindingServiceConfiguration::class)
@EnableBinding(UpdateRouteInput::class, RouteLogOutput::class)
class RouteConfiguration @Autowired
constructor(private val environment: Environment, private val bindings: BindingServiceProperties, private val routeLogDomain: RouteLogDomain) {

    @PostConstruct
    fun init() {
        initConsumer()
        initProducer()
    }

    private fun initConsumer() {
        if (this.bindings.bindings[GateWayConstant.UPDATE_ROUTE_INPUT] == null) {
            this.bindings.bindings[GateWayConstant.UPDATE_ROUTE_INPUT] = BindingProperties()
        }
        this.bindings.bindings[GateWayConstant.UPDATE_ROUTE_INPUT]?.let {
            if (it.destination == null || it.destination == GateWayConstant.UPDATE_ROUTE_INPUT) {
                it.destination = GateWayConstant.UPDATE_ROUTE_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
            it.group = GateWayConstant.UPDATE_ROUTE_GROUP_PREFIX + environment.getProperty("server.address") + "-" + environment.getProperty("server.port")
        }
    }

    private fun initProducer() {
        if (this.bindings.bindings[GateWayConstant.ROUTE_LOG_OUTPUT] == null) {
            this.bindings.bindings[GateWayConstant.ROUTE_LOG_OUTPUT] = BindingProperties()
        }
        this.bindings.bindings[GateWayConstant.ROUTE_LOG_OUTPUT]?.let {
            if (it.destination == null || it.destination == GateWayConstant.ROUTE_LOG_OUTPUT) {
                it.destination = GateWayConstant.ROUTE_LOG_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
        }
    }

    @Bean
    fun updateRouteConsumer(routeRedisRepository: RouteRedisRepository) = UpdateRouteConsumer(routeRedisRepository)

    @Bean
    fun corsFilter(): WebFilter {
        return object : WebFilter {
            override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
                val request = exchange.request
                if (CorsUtils.isCorsRequest(request)) {
                    val response = exchange.response
                    val headers = response.headers
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN)
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS)
                    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE)
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS)
                    headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALLOWED_Expose)
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                    if (request.method == HttpMethod.OPTIONS) {
                        response.statusCode = HttpStatus.OK
                        return Mono.empty()
                    }
                }
                return chain.filter(exchange)
            }
        }
    }

    @Bean
    @Order(LoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER + 100)
    fun countFilter(): GlobalFilter = GlobalFilter { exchange, chain ->
        val routeLogPO = routeLogDomain.beforeRoute(exchange)
        chain!!.filter(exchange!!)
                .then(Mono.just<ServerWebExchange>(exchange))
                .map { serverWebExchange ->
                    routeLogDomain.afterRoute(exchange, routeLogPO)
                    serverWebExchange
                }.then()
    }

    companion object {

        private const val ALLOWED_HEADERS = "Content-Type,Content-Length,Authorization,Accept,X-Requested-With,Origin,Referer,User-Agent,Process400,Process401,Process403"

        private const val ALLOWED_METHODS = "*"

        private const val ALLOWED_ORIGIN = "*"

        private const val ALLOWED_Expose = "*"

        private const val MAX_AGE = "18000L"
    }

}