package org.luis.sainteclaires.customer.rest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.luis.basic.domain.FilterAttributes;
import org.luis.basic.rest.model.SimpleMessage;
import org.luis.basic.rest.model.SimpleMessageHead;
import org.luis.basic.util.SpringContextFactory;
import org.luis.sainteclaires.base.INameSpace;
import org.luis.sainteclaires.base.bean.Account;
import org.luis.sainteclaires.base.bean.Address;
import org.luis.sainteclaires.base.bean.Category;
import org.luis.sainteclaires.base.bean.Order;
import org.luis.sainteclaires.base.bean.OrderItem;
import org.luis.sainteclaires.base.bean.ProductShot;
import org.luis.sainteclaires.base.bean.ProductVo;
import org.luis.sainteclaires.base.bean.ShoppingBag;
import org.luis.sainteclaires.base.bean.service.OrderService;
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

	@RequestMapping("login")
	public String login() {
		return "customer/login";
	}

	/**
	 * 跳到注册页面
	 * 
	 * @return
	 */
	@RequestMapping("register")
	public String register() {
		return "customer/register";
	}

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

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest req) {
		req.getSession().removeAttribute(INameSpace.KEY_SESSION_CUSTOMER);
		req.getSession().removeAttribute("userName");
		return "redirect:/index";
	}

	/**
	 * 订单确认
	 * 
	 * @param req
	 * @param map
	 * @return
	 */
	@RequestMapping("order/confirm")
	public String submitOrder(HttpServletRequest req, ModelMap map) {
		String userName = BaseUtil.getLoginName(req);
		ShoppingBag bag = (ShoppingBag) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CART);
		if(bag != null){
			Order order = orderService.createOrder(bag, userName);
			req.getSession().removeAttribute(INameSpace.KEY_SESSION_CART);
			map.put("order", order);
		} else {
			List<Order> orders = orderService.findUnpayOrders(userName);
			map.put("order", orders.get(orders.size() - 1));
		}
		setAddress(map, userName);
		return "customer/submit_order";
	}

	/**
	 * 跳转到订单查看页面
	 * 
	 * @return
	 */
	@RequestMapping("orders")
	public String orders(HttpServletRequest req, ModelMap map) {
		String userName = BaseUtil.getLoginName(req);
		List<Order> orders = orderService.findUnpayOrders(userName);
		map.put("orders", orders);
		setAddress(map, userName);
		return "customer/orders";
	}

	/**
	 * 帐号验证
	 * 
	 * @param account
	 * @return
	 */
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
		if (!sm.getHead().getRep_code().equals(SimpleMessageHead.REP_OK)) {
			map.put("error", sm.getHead().getRep_message());
			return "customer/register";
		}
		return "redirect:/index";
	}

	/**
	 * 修改密码
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @param confirmPwd
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "password/change", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<?> changePassword(String oldPwd, String newPwd,
			String confirmPwd, HttpServletRequest req) {
		Account account = (Account) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CUSTOMER);
		SimpleMessage<?> sm = BaseUtil.getAccountService().changePassword(
				account, oldPwd, newPwd, confirmPwd);
		return sm;
	}

	/**
	 * 添加商品到购物车
	 * 
	 * @param bagId
	 * @param shotId
	 * @return
	 */
	@RequestMapping(value = "shot/add", method = RequestMethod.POST)
	public String addProductToBag(ProductShot shot, HttpServletRequest req,
			ModelMap map) {
		ShoppingBag bag = (ShoppingBag) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CART);
		ProductVo vo = BaseUtil.getProductVoService().get(shot.getProductId());
		shot.setPic(vo.getPicList().get(0));
		shot.setProductName(vo.getName());
		shot.setPrice(vo.getPrice());
		if (bag == null) {
			bag = new ShoppingBag();
			bag.getProductShots().add(shot);
			if (BaseUtil.getSessionAccount(req) != null) {
				bag.setCustNo(BaseUtil.getSessionAccount(req).getLoginName());
			}
			BaseUtil.setSessionAttr(req, INameSpace.KEY_SESSION_CART, bag);
		} else {
			bag.getProductShots().add(shot);
		}
		bag.setTotalAmount(bag.getTotalAmount().add(
				shot.getPrice().multiply(BigDecimal.valueOf(shot.getNumber()))));
		
		SimpleMessage<ShoppingBag> sm = new SimpleMessage<ShoppingBag>();
		sm.setItem(bag);
		setModel(map);
		// SimpleMessage<?> sm = new SimpleMessage<Object>();
		// ServiceFactory.getProductShotService().save(shot);
		return "redirect:/detail?id=" + vo.getId();
	}

	/**
	 * 修改购物车商品
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping(value = "shot/edit/{productId}/{num}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<ShoppingBag> editItem(
			@PathVariable("productId") Long productId,
			@PathVariable("num") Integer num, HttpServletRequest req) {
		ShoppingBag bag = (ShoppingBag) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CART);
		for (ProductShot shot : bag.getProductShots()) {
			if (shot.getProductId().equals(productId)) {
				int n = num - shot.getNumber();
				shot.setNumber(num);
				bag.setTotalAmount(bag.getTotalAmount().add(
						shot.getPrice().multiply(BigDecimal.valueOf(n))));
				break;
			}
		}
		SimpleMessage<ShoppingBag> sm = new SimpleMessage<ShoppingBag>();
		sm.setItem(bag);
		// ProductShot entity = new ProductShot();
		// entity.setId(shotId);
		// entity.setNumber(num);
		// ServiceFactory.getProductShotService().update(entity);
		return sm;
	}

	/**
	 * 删除购物车商品
	 * 
	 * @param shotId
	 * @return
	 */
	@RequestMapping(value = "shot/delete/{productId}", method = RequestMethod.GET)
	public String deleteItem(@PathVariable("productId") Long productId,
			HttpServletRequest req) {
		ShoppingBag bag = (ShoppingBag) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CART);
		ProductShot temp = null;
		for (ProductShot shot : bag.getProductShots()) {
			if (shot.getProductId().equals(productId)) {
				temp = shot;
				break;
			}
		}
		bag.setTotalAmount(bag.getTotalAmount().subtract(temp.getPrice()));
		bag.getProductShots().remove(temp);
		SimpleMessage<ShoppingBag> sm = new SimpleMessage<ShoppingBag>();
		sm.setItem(bag);
		// ProductShot entity = new ProductShot();
		// entity.setId(shotId);
		// ServiceFactory.getProductShotService().delete(entity);
		return "redirect:/detail?id=" + productId;
	}

	/**
	 * 管理收货地址
	 * 
	 * @return
	 */
	@RequestMapping("address")
	public String address(ModelMap map, HttpServletRequest request) {
		Account account = (Account) request.getSession().getAttribute(
				INameSpace.KEY_SESSION_CUSTOMER);
		FilterAttributes fa = FilterAttributes.blank().add("loginName",
				account.getLoginName());
		List<Address> list = ServiceFactory.getAddressService()
				.findByAttributes(fa);
		map.put("addresses", list);
		map.put("succ", request.getParameter("succ"));
		return "customer/addressmg";
	}

	/**
	 * 账户管理
	 * 
	 * @return
	 */
	@RequestMapping("account")
	public String account(ModelMap map, HttpServletRequest request) {
		Account account = (Account) request.getSession().getAttribute(
				INameSpace.KEY_SESSION_CUSTOMER);
		map.put(INameSpace.KEY_SESSION_CUSTOMER, account);
		map.put("succ", request.getParameter("succ"));
		return "customer/accountmg";
	}

	/**
	 * 保存账号信息
	 * 
	 * @param account
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "account/save", method = RequestMethod.POST)
	@ResponseBody
	public SimpleMessage<?> accountSave1(Account account, HttpServletRequest req,ModelMap map){
		String loginName = (String) req.getSession().getAttribute("userName");
		Account a = BaseUtil.getAccountService().getAccount(loginName);
		a.setCustName(account.getCustName());
		a.setEmail(account.getEmail());
		a.setPhone(account.getPhone());
		SimpleMessage<?> sm = BaseUtil.getAccountService().updateAccount(a);
		if(sm.getHead().getRep_code().endsWith(SimpleMessageHead.REP_OK)){
			req.getSession().setAttribute(INameSpace.KEY_SESSION_CUSTOMER, a);
		}
		return sm;
	}

	/**
	 * 密码修改
	 * 
	 * @return
	 */
	@RequestMapping("password")
	public String password(ModelMap map, HttpServletRequest request) {
		map.put("succ", request.getParameter("succ"));
		return "customer/passwordmg";
	}

	// /////////////订单操作///////////////////
	/**
	 * 从购物车创建订单
	 * 
	 * @param shotId
	 * @return
	 */
	@RequestMapping(value = "order/create/{bagId}", method = RequestMethod.GET)
	public String createOrder(@PathVariable("bagId") String bagId) {
		return "";
	}

	/**
	 * 订单某一商品修改
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping(value = "order/item/edit/{itemId}/{num}", method = RequestMethod.GET)
	// @ResponseBody
	public String editItemInOrder(@PathVariable("itemId") Long itemId,
			@PathVariable("num") int num) {
		OrderItem entity = new OrderItem();
		entity.setId(itemId);
		entity.setNum(num);
		ServiceFactory.getOrderDetailService().update(entity);
		return "redirect:/orders";
	}

	/**
	 * 从订单删除商品
	 * 
	 * @param orderId
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value = "order/item/delete/{itemId}", method = RequestMethod.GET)
	public String deleteItemInOrder(@PathVariable("itemId") Long itemId) {
		OrderItem entity = new OrderItem();
		entity.setId(itemId);
		ServiceFactory.getOrderDetailService().delete(entity);
		return "redirect:/orders";
	}

	/**
	 * 保存地址
	 * 
	 * @param address
	 * @return
	 */
	@RequestMapping(value = "address/save", method = RequestMethod.POST)
	public String saveAddress(Address address, ModelMap map,
			HttpServletRequest request) {
		address.setLoginName((String) request.getSession().getAttribute(
				"userName"));
		boolean b = ServiceFactory.getAddressService().save(address);
		map.put("succ", b);
		return "redirect:/address";
		// return sm;
	}

	/**
	 * 查询地址
	 * 
	 * @return
	 */
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

	/**
	 * 设置默认地址
	 * 
	 * @param id
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "address/default/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleMessage<?> setDefaultAddress(@PathVariable("id") long id,
			HttpServletRequest req) {
		Account account = (Account) req.getSession().getAttribute(
				INameSpace.KEY_SESSION_CUSTOMER);
		Account a = BaseUtil.getAccountService().getAccount(
				account.getLoginName());
		a.setAddressId(id);
		boolean flag = BaseUtil.getAccountService().update(a);
		if (flag == true) {
			req.getSession().setAttribute(INameSpace.KEY_SESSION_CUSTOMER, a);
			return new SimpleMessage<Object>();
		}
		return new SimpleMessage<Object>(new SimpleMessageHead(
				SimpleMessageHead.REP_SERVICE_ERROR, "设置默认地址失败"));
	}

	private void setModel(ModelMap map) {
		List<Category> parents = BaseUtil.getParentCates();
		Map<Long, List<Category>> subcatMap = BaseUtil.getSubCatsMap();
		map.put("parents", parents);
		map.put("subcatMap", subcatMap);
	}

	private void setAddress(ModelMap map, String userName) {
		FilterAttributes fa = FilterAttributes.blank().add("loginName",
				userName);
		List<Address> list = ServiceFactory.getAddressService()
				.findByAttributes(fa);
		map.put("addresses", list);
	}

	private OrderService orderService = SpringContextFactory
			.getSpringBean(OrderService.class);

}
