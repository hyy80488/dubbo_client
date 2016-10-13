<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/TagLib.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Page</title>
</head>
<body>
	<h1>Result</h1>
	<%--用于输入后台返回的验证错误信息 --%>
	<a href="${ctx}/login.jsp">back</a>
	<a href="${ctx}/security/dologout">logout</a>
	<P>${msg}</P>
	<P>${userName}</P>
	<P>${message}</P>
	<% out.println(request.getLocalAddr() + " : " + request.getLocalPort()+"<br>");%>
	<% out.println("ID : " + session.getId()+"<br>");%>
</body>
</html>