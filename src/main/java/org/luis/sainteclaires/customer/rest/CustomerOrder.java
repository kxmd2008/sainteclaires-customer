package org.luis.sainteclaires.customer.rest;

import org.luis.sainteclaires.base.bean.Order;
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
		OrderItem nitem = ServiceFactory.getOrderDetailService().get(item.getId());
		//更新原订单item状态为完成
		nitem.setStatus(OrderItem.STATUS_COMPLETE);
		ServiceFactory.getOrderDetailService().update(item);
		
		Order order = ServiceFactory.getOrderService().get(nitem.getOrderId());
		order.setStatus(OrderItem.STATUS_COMPLETE);
		ServiceFactory.getOrderService().save(order);
		
		
		//创建新订单
		order.setId(null);
		order.setStatus(Order.STATUS_RETURN);
		ServiceFactory.getOrderService().save(order);
		nitem.setStatus(OrderItem.STATUS_RETURN);
		nitem.setId(null);
		nitem.setOrderId(order.getId());
		nitem.setNote(item.getNote());
		ServiceFactory.getOrderDetailService().save(nitem);
		return "redirect:/orders";
	}
	
}
