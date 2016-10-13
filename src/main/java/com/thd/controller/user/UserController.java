package com.thd.controller.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thd.bean.CommForm;
import com.thd.bean.User;
import com.thd.controller.PubController;
import com.thd.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController extends PubController{
	
	@Resource
	private UserService userService;
	
	@RequestMapping(value = "/findUser",method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response,CommForm commForm) throws Exception{
		System.out.println("====client=================="+commForm.getUserName());
		Map<String,Object> model = new HashMap<String,Object>();
		User u = userService.findUser(commForm.getUserName());
		model.put("msg","Hello World !");
		model.put("user", u);
		return new ModelAndView("/user",model);
	}
}
