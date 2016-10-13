<%@ page import="com.core.util.RequestUtil" pageEncoding="UTF-8"%>
<%@ include file="/TagLib.jsp"%>
<%
	String errorUrl = RequestUtil.getErrorUrl(request);
	//boolean isContent = (errorUrl.endsWith(".html") || errorUrl.endsWith(".jsp"));
	response.addHeader("__400_error", "true");
	response.setStatus(400);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>404错误</title>
</head>

<body>
	<div class="error">
		<div class="error_logo">
			<img src="${ctx}/images/error.jpg" />
		</div>
		<div class="error_box">
			<div class="error_info">
				<p>
					很抱歉，出错了！<br /> <%=errorUrl%>不存在！
				</p>
			</div>
			<div class="error_link">
				<a class="back" href="${ctx}/login.jsp" target="_parent">返回首页</a>
			</div>
		</div>
	</div>

</body>
</html>