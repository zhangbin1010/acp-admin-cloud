package pers.acp.admin.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import pers.acp.core.CommonTools;
import pers.acp.admin.oauth.component.UserPasswordEncoder;
import pers.acp.admin.oauth.domain.security.SecurityUserDetailsDomain;
import pers.acp.springcloud.common.constant.ConfigurationOrder;

/**
 * @author zhangbin by 11/04/2018 15:16
 * @since JDK 11
 */
@Configuration
@EnableWebSecurity
@Order(ConfigurationOrder.resourceServerConfiguration + 1)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final String contextPath;

    private final UserPasswordEncoder userPasswordEncoder;

    private final SecurityUserDetailsDomain userDetailsService;

    @Autowired
    public WebSecurityConfiguration(ServerProperties serverProperties, UserPasswordEncoder userPasswordEncoder, SecurityUserDetailsDomain userDetailsService) {
        this.userPasswordEncoder = userPasswordEncoder;
        this.contextPath = CommonTools.isNullStr(serverProperties.getServlet().getContextPath()) ? "" : serverProperties.getServlet().getContextPath();
        this.userDetailsService = userDetailsService;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(userPasswordEncoder);
    }

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers(
                contextPath + "/error",
                contextPath + "/actuator",
                contextPath + "/actuator/**",
                contextPath + "/oauth/authorize",
                contextPath + "/oauth/token",
                contextPath + "/oauth/check_token",
                contextPath + "/oauth/confirm_access",
                contextPath + "/oauth/error").permitAll()
                .anyRequest().authenticated();
    }

}
