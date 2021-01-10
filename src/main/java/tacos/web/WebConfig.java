package tacos.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	//뷰 컨트롤러 선언 (요청을 특정 뷰로 연결만 해주는 controller 역할을 수행)
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	  registry.addViewController("/").setViewName("home");
	  registry.addViewController("/login");
	}

}
