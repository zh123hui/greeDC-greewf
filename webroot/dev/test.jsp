<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%

try{
    java.net.URL resource = org.apache.axis.client.Service.class.getClassLoader().getResource("org/apache/axis/client/Service");
    response.getWriter().write("org.apache.axis.client.Service路径: "+resource.getPath());
}catch (Throwable e) {
    e.printStackTrace();
}

%>
</body>
</html>