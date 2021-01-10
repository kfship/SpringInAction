package tacos.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.User;
import tacos.data.OrderRepository;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

	private OrderRepository orderRepo;
	
	@Autowired
	public OrderController(OrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}
	
	@GetMapping("/current")
	public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute Order order) {
		
		if (order.getDeliveryName() == null) {
			order.setDeliveryName(user.getFullname());
		}
		
		if (order.getDeliveryStreet() == null) {
			order.setDeliveryStreet(user.getStreet());
		}
		
		if (order.getDeliveryCity() == null) {
			order.setDeliveryCity(user.getCity());
		}
		
		if (order.getDeliveryState() == null) {
			order.setDeliveryState(user.getState());
		}
		
		if (order.getDeliveryZip() == null) {
			order.setDeliveryZip(user.getZip());
		}
		
		return "orderForm";
	}

	/**
	 * User 정보는 스프링시큐리티에서 관리하고 있는 로그인 회원정보를 바로 가져오는 것임
	 * 
	 * 
	 * @param order
	 * @param errors
	 * @param sessionStatus
	 * @param user
	 * @return
	 */
	@PostMapping
	public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
		if (errors.hasErrors()) {
			return "orderForm";
		}
		
		/* 인증된 사용자 정보를 이렇게 가지고 올 수 있다. 이 방법의 장점은 컨트롤러의 처리 메소드가 아니어도 어디서든 사용 가능하다는 것이다
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		그 밖에 다음과 같은 방법들을 쓸 수 있다
		  - Principal 객체를 컨트롤러 메서드에 주입한다.
		  - Authentication 객체를 컨트롤러 메서드에 주입한다.
		  - @AuthenticationPrincipal 애노테이션을 메서드에 지정한다.
		*/
				
		order.setUser(user);
		
		log.info("Order submitted: " + order);
		
		orderRepo.save(order);
		
		sessionStatus.setComplete();  //session에 있는 값을 초기화 해준다
		return "redirect:/";
	}

}
