package com.thd.controller.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.thd.bean.CommForm;
import com.thd.bean.User;
import com.thd.controller.PubController;
import com.thd.service.UserService;

@Controller
@RequestMapping(value = "/security")
public class LoginController extends PubController {

	@Resource
	private UserService userService;
	
	@RequestMapping(value = "/dologin")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, CommForm commForm) throws Exception {
		String msg = "";
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		System.out.println(userName);
		System.out.println(password);
		UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
		token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();
		
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("msg","Hello World !");
		model.put("userName",userName);
		
		try {
			subject.login(token);
			if (subject.isAuthenticated()) {
				System.out.println("isAuthenticated!!!!!!");
				
				User u = userService.findUserByName(commForm.getUserName());
				model.put("user", u);
				return new ModelAndView("/user",model);
			} else {
				return new ModelAndView("/result",model);
			}
		} catch (IncorrectCredentialsException e) {
			msg = "登录密码错误. Password for account " + token.getPrincipal() + " was incorrect.";
			model.put("message", msg);
			System.out.println(msg);
		} catch (ExcessiveAttemptsException e) {
			msg = "登录失败次数过多";
			model.put("message", msg);
			System.out.println(msg);
		} catch (LockedAccountException e) {
			msg = "帐号已被锁定. The account for username " + token.getPrincipal() + " was locked.";
			model.put("message", msg);
			System.out.println(msg);
		} catch (DisabledAccountException e) {
			msg = "帐号已被禁用. The account for username " + token.getPrincipal() + " was disabled.";
			model.put("message", msg);
			System.out.println(msg);
		} catch (ExpiredCredentialsException e) {
			msg = "帐号已过期. the account for username " + token.getPrincipal() + "  was expired.";
			model.put("message", msg);
			System.out.println(msg);
		} catch (UnknownAccountException e) {
			msg = "帐号不存在. There is no user with username of " + token.getPrincipal();
			model.put("message", msg);
			System.out.println(msg);
		} catch (UnauthorizedException e) {
			msg = "您没有得到相应的授权！" + e.getMessage();
			model.put("message", msg);
			System.out.println(msg);
		}
//		return new ModelAndView("redirect:/login.jsp",model);
		return new ModelAndView("/result",model);
	}
	
	@RequestMapping(value = "/dologout")
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response, CommForm commForm) throws Exception {
		Map<String,Object> model = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
			return new ModelAndView("redirect:/login.jsp",model);
		}else{
			return new ModelAndView("redirect:/login.jsp",model);
		}
	}
}
