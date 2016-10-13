<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
	String ctx = request.getContextPath();
	pageContext.setAttribute("ctx",ctx);
	pageContext.setAttribute("ctxMenu",ctx+"/images/menu");
	String lan=(String)request.getSession().getAttribute("language");
	String frame_theme=(String)request.getSession().getAttribute("frame_theme");
	
	if(lan!=null&&"US".equals(lan)){
		lan="en";
	}else{
		lan="cn";
	}
	pageContext.setAttribute("language", lan);
	pageContext.setAttribute("frame_theme", frame_theme);
%>
