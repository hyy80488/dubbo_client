<%@page import="com.core.util.RequestUtil" pageEncoding="UTF-8"%>
<%@ include file="/TagLib.jsp"%>
<%
	String errorUrl = RequestUtil.getErrorUrl(request);
	response.addHeader("__400_error","true");
	response.setStatus(400);
%>
<html>
	<head>
		<title>访问拒绝</title>
			<style type="text/css">
			<!--
			.STYLE10 {
				font-family: "黑体";
				font-size: 36px;
			}
			-->  
			</style>
	</head>
	<body>
	 <table width="510" border="0" align="center" cellpadding="0" cellspacing="0">
	  <tr>
    	<td><img src="${ctx}/images/error_top.jpg" width="510" height="80" /></td>
  	  </tr>
	  <tr>
	    <td height="200" align="center" valign="top" background="${ctx}/images/error_bg.jpg">
	    	<table width="80%" border="0" cellspacing="0" cellpadding="0">
	        <tr>
	          <td width="34%" align="right"><img src="${ctx}/images/error.gif" width="128" height="128"></td>
	          <td width="66%" valign="bottom" align="center">
	          	<span class="STYLE10">访问被拒绝</span>
	          	<div style="text-align: left;line-height: 22px;">
	            <font size="2">对不起，内部服务器错误。点击这里返回主页。如果需要技术支持，点击这里发送邮件。</font>
		        </div>
		        <a href="#" onclick="javascript:document.location.href='${ctx}/login.jsp';">重 新 登 录</a> 
		        <a href="#" onclick="javascript:history.back(-1);">后 退</a>
	     	 </td>
	      </table>
	      </td>
	  </tr>    	 
	  <tr>
    	<td><img src="${ctx}/images/error_bootom.jpg" width="510" height="32" /></td>
      </tr>
	</table>
	</body>
</html>