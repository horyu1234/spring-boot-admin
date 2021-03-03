package me.horyu.springbootadmin

import de.codecentric.boot.admin.server.config.AdminServerProperties
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.util.*

@Configuration(proxyBeanMethods = false)
class WebSecurityConfig(
    private val adminServer: AdminServerProperties,
    private val security: SecurityProperties
) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        val successHandler = SavedRequestAwareAuthenticationSuccessHandler()
        successHandler.setTargetUrlParameter("redirectTo")
        successHandler.setDefaultTargetUrl(adminServer.path("/"))

        http.authorizeRequests {
            it.antMatchers(adminServer.path("/assets/**")).permitAll()
                .antMatchers(adminServer.path("/actuator/info")).permitAll()
                .antMatchers(adminServer.path("/actuator/health")).permitAll()
                .antMatchers(adminServer.path("/login")).permitAll()
                .anyRequest().authenticated()
        }.formLogin {
            it.loginPage(
                adminServer.path("/login")
            ).successHandler(successHandler).and()
        }.logout {
            it.logoutUrl(
                adminServer.path("/logout")
            )
        }.httpBasic(Customizer.withDefaults())
            .csrf {
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        AntPathRequestMatcher(
                            adminServer.path("/instances"),
                            HttpMethod.POST.toString()
                        ),
                        AntPathRequestMatcher(
                            adminServer.path("/instances/*"),
                            HttpMethod.DELETE.toString()
                        ),
                        AntPathRequestMatcher(adminServer.path("/actuator/**"))
                    )
            }
            .rememberMe {
                it
                    .key(UUID.randomUUID().toString())
                    .tokenValiditySeconds(1209600)
            }
    }

    // Required to provide UserDetailsService for "remember functionality"
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser(security.user.name)
            .password("{noop}" + security.user.password)
            .roles("USER")
    }
}