package org.luis.sainteclaires.customer.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.luis.basic.domain.FilterAttributes;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.luis.sainteclaires.base.INameSpace;
import org.luis.sainteclaires.base.bean.Account;
import org.luis.sainteclaires.base.bean.Address;
import org.luis.sainteclaires.base.bean.service.ServiceFactory;
import org.luis.sainteclaires.base.util.BaseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class CustomerRest {

	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(String loginName, String password, ModelMap map,
			HttpServletRequest req) {
		SimpleMessage<Account> sm = BaseUtil.getAccountService().login(
				loginName, password);
		if (!sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)) {
			map.put("errorMsg", sm.getHead().getRep_message());
			return "customer/login";
		}
		req.getSession().setAttribute(INameSpace.KEY_SESSION_CUSTOMER,
				sm.getItem());
		return "common/index";
	}

	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(HttpServletRequest req) {
		req.getSession().removeAttribute(INameSpace.KEY_SESSION_CUSTOMER);
		return "redirect:/common/index";
	}

	/**
	 * 跳到注册页面
	 * 
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String register(Account account) {
		return "customer/register";
	}

	@RequestMapping(value = "account/check", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<?> accountCheck(Account account) {
		SimpleMessage<?> sm = BaseUtil.getAccountService().checkLoginName(
				account.getLoginName());
		return sm;
	}

	/**
	 * 创建用户
	 * 
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "account/create", method = RequestMethod.POST)
	public String accountCreate(Account account, ModelMap map) {
		SimpleMessage<?> sm = BaseUtil.getAccountService().registion(account);
		if(!sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)){
			map.put("error", sm.getHead().getRep_message());
			return "customer/register";
		}
		return "redirect:/common/index";
	}

	@RequestMapping(value = "changePassword", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage changePassword(String oldPwd, String newPwd,
			String confirmPwd) {
		SimpleMessage sm = new SimpleMessage();

		return sm;
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
	public String createOrder(@PathVariable("shotId") String shotId) {
		SimpleMessage sm = new SimpleMessage();

		return "";
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

	@RequestMapping(value = "address/save", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<Address> saveAddress(Address address) {
		SimpleMessage<Address> sm = new SimpleMessage<Address>();
		boolean b = ServiceFactory.getAddressService().save(address);
		if (!b) {
			sm.getHead().setRep_code("1002");
			sm.getHead().setRep_message("地址保存失败");
		}
		return sm;
	}

	@RequestMapping(value = "addresses", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<Address> getAddress(HttpServletRequest req) {
		Account account = (Account) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CUSTOMER);
		FilterAttributes fa = FilterAttributes.blank().add("loginName",
				account.getLoginName());
		List<Address> list = ServiceFactory.getAddressService()
				.findByAttributes(fa);
		SimpleMessage<Address> sm = new SimpleMessage<Address>();
		sm.setRecords(list);
		return sm;
	}

}
