package org.luis.sainteclaires.customer.rest;

import org.luis.sainteclaires.base.bean.OrderItem;
import org.luis.sainteclaires.base.bean.service.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class CustomerOrder {

	@RequestMapping(value = "exchange/{orderNo}/{itemId}", method=RequestMethod.GET)
	public String exchange(@PathVariable("orderNo") String orderNo,
			@PathVariable("itemId") Long itemId, ModelMap map) {
		map.put("orderNo", orderNo);
		map.put("itemId", itemId);
		return "common/changes";
	}
	
	@RequestMapping(value = "exchange/submit", method=RequestMethod.POST)
	public String exchangeSubmit(OrderItem item, ModelMap map) {
		item = ServiceFactory.getOrderDetailService().get(item.getId());
		item.setStatus(OrderItem.STATUS_RETURN);
		ServiceFactory.getOrderDetailService().update(item);
		return "customer/orders";
	}
	
}
