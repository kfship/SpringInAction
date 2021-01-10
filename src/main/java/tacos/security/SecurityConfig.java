package tacos.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	DataSource dataSource;
	
	
	/**
	 * HTTP 요청 처리를 허용하기 전에 충족되어야 할 특정 보안 조건을 구성한다
	 * 커스텀 로그인 페이지를 구성한다
	 * 사용자가 애플리케이션의 로그아웃을 할 수 있도록 한다
	 * CSRF 공격으로부터 보호하도록 구성한다
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
		http.authorizeRequests().antMatchers("/design", "/orders")  //먼저 지정된 경로를 우선적으로 처리한다
								.access("hasRole('ROLE_USER')")     //access 메소드 안에 SpEL (Spring Expression Language) 사용
								.antMatchers("/", "/**")
								.access("permitAll")
								.and()								//인증 구성이 끝남. 추가적인 HTTP구성을 적용하겠다는 의미
									.formLogin()					//커스텀 로그인 폼을 구성하기 위해 호출
									.loginPage("/login")			//커스텀 로그인 페이지 경로
									.defaultSuccessUrl("/design")	//명시적으로 로그인 화면에 들어온 경우에 로그인 후 이동할 페이지 지정  (명시적이지 않다면 원래 있던 화면으로 돌아감)
								.and()								//		만약 ("/design", true) 로 호출하면 명시적 호출과 상관없이 무조건 로그인 후 이동
									.logout()
									.logoutSuccessUrl("/")			//지정하지 않는다면 로그인 페이지로 이동한다
								.and()
									.csrf();
								
		
								/*
								.loginProcessingUrl("/authenticate")//로그인 화면에서 submit할 때 해당 요청을 받아줄 경로
								.usernameParameter("user")			//로그인 화면에서 사용자 ID 필드명
								.passwordParameter("pwd")			//로그인 화면에서 사용자 비밀번호 필드명
								 */
		
								/*
								.and()								//csrf를 비활성화 할 경우, 가능하면 하지 말자
								.csrf()
								.disable();
								*/

	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		//UserRepositoryUserDetailsService 클래스에서 username으로 UserDetails를 반환하는 로직을 구현한다   
		auth.userDetailsService(userDetailsService)
		.passwordEncoder(encoder());  //이렇게 하면 객체를 바로 생성해서 주입하는것과 달리, 이 인스턴스는 애플리케이션 컨텍스트로부터 주입된다.
		
		/* in-memory 방식

		auth.inMemoryAuthentication()
				.withUser("user1")
				.password("{noop}password1")
				.authorities("ROLE_USER")
				.and()
				.withUser("user2")
				.password("{noop}password2")
				.authorities("ROLE_USER");
		*/
		
		/* JDBC를 이용한 방식, 스프링 시큐리티에서는 무조건 비밀번호 인코딩을 수행해야 한다.
		 * 아래 예제에서는 테스트를 용이하게 하기 위해 아무작업도 하지 않는 인코더를 생성하여 사용함
		
		auth.jdbcAuthentication()
				.dataSource(dataSource)
				.passwordEncoder(new NoEncodingPasswordEncoder());
				//.passwordEncoder(new BCryptPasswordEncoder());
		*/
	}
}
