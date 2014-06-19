package org.luis.sainteclaires.customer.rest;

import java.util.List;

import org.luis.basic.rest.model.SimpleMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sainteclaires.base.bean.Category;
import com.sainteclaires.base.util.BaseUtil;

@Controller
@RequestMapping("/")
public class CustomerRest {

	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage login(String loginName, String password) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "logout", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage logout(String loginName) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "changePassword", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage changePassword(String oldPwd, String newPwd,
			String confirmPwd) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}
	
	@RequestMapping(value = "changePassword", method = RequestMethod.GET)
	@ResponseBody
	public List<Category> getParentCates(){
		return BaseUtil.getParentCates();
	}
	
	@RequestMapping(value = "changePassword", method = RequestMethod.GET)
	@ResponseBody
	public List<Category> getSubCates(Long parentId){
		return BaseUtil.getSubCates(parentId);
	}

	@RequestMapping(value = "bag/addItem/{shotId}/{itemId}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage addProductToBag(@PathVariable("shotId") String shotId,
			@PathVariable("itemId") String itemId) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "bag/editItem/{itemId}/{num}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage editItem(@PathVariable("itemId") String itemId,
			@PathVariable("num") int num) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "bag/deleteItem/{shotId}/{itemId}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteItem(@PathVariable("shotId") String shotId,
			@PathVariable("itemId") String itemId) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "createOrder/{shotId}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage createOrder(@PathVariable("shotId") String shotId) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "order/editItem/{itemId}/{num}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage editItemInOrder(@PathVariable("itemId") String itemId,
			@PathVariable("num") int num) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

	@RequestMapping(value = "order/deleteItem/{orderId}/{itemId}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage deleteItemInOrder(
			@PathVariable("orderId") String orderId,
			@PathVariable("itemId") String itemId) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
	}

}
