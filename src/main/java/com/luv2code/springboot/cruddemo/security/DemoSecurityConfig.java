package com.luv2code.springboot.cruddemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class DemoSecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?"
        );
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?"
        );

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(configuner ->
                configuner
                        .requestMatchers(HttpMethod.GET,"/api/employees").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET,"/api/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST,"/api/employees/").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/employees/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE,"/api/employees/**").hasRole("ADMIN")


                );

        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
     /*
    @Bean
    public InMemoryUserDetailsManager userDetailsManager(){
        UserDetails henry = User.builder()
                .username("henry")
                .password("{noop}test123")
                .roles("EMPLOYEE")
                .build();

        UserDetails mert=User.builder()
                .username("mert")
                .password("{noop}test123")
                .roles("EMPLOYEE","MANAGER")
                .build();

        UserDetails berk = User.builder()
                .username("berk")
                .password("{noop}test123")
                .roles("EMPLOYEE","MANAGER","ADMIN")
                .build();
        return new InMemoryUserDetailsManager(henry,mert,berk);
    }
*/
}
//{noop} ifadesi, şifrenin şifrelenmediğini ve doğrudan düz metin (plain text) olarak kullanılacağını belirten bir göstergedir.
//Şifreleme işlemi kullanılmadığında,
//şifreyi belirli bir algoritma ile şifrelemeden doğrudan kullanabilmeniz için bu yapı kullanılır.
//SecurityFilterChain: Bu, güvenlik filtrelerinin zincirini ifade eder. Spring Security, her isteğe belirli güvenlik
// filtrelerini uygular ve burada bu filtre zincirini yapılandırıyoruz.
//HttpSecurity: Bu, HTTP isteklerine nasıl güvenlik uygulayacağınızı yapılandırmanıza izin veren bir sınıftır. Yetkilendirme
//(authorization), kimlik doğrulama (authentication), CSRF koruması gibi pek çok güvenlik özelliğini buradan yönetiriz.
//authorizeHttpRequests(): Burada, hangi HTTP isteklerinin kimler tarafından (hangi roller ya da kullanıcılar)
//yetkilendirileceğini belirleyebilirsin. Yani, belirli URL'lere kimlerin erişebileceğini tanımlıyorsun.
//"/api/employees/**" ifadesi, /api/employees ile başlayan tüm URL'leri temsil eder.