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
		//创建新订单
		nitem.setStatus(OrderItem.STATUS_RETURN);
		nitem.setId(null);
		nitem.setNote(item.getNote());
		ServiceFactory.getOrderDetailService().save(nitem);
		Order order = ServiceFactory.getOrderService().get(nitem.getOrderId());
		order.setId(null);
		ServiceFactory.getOrderService().save(order);
		return "customer/orders";
	}
	
}
