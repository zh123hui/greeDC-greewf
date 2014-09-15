<%@page import="com.gree.mobile.wf.taskinfo.template.grid.GridView"%>
<%@page import="com.gree.mobile.wf.taskinfo.template.grid.GridViewWriter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<meta content="telephone=no" name="format-detection" />
<meta name="apple-mobile-web-app-capable" content="yes">
<title>详情</title>
<style>
body{
    width: 400px;
}
.table_search {
	width: 100%;
	clear: both;
	border: #d2d0d0 1px solid;
	border-collapse: collapse;
	border-spacing: 0;
}
.rotate90{
	height: 400px;
	-webkit-transform: rotate(90deg);
    -moz-transform: rotate(90deg);
    filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=1);
}

.table_search tr,.table_search tbody {
	margin: 0;
	padding: 0;
	border: 0;
	outline: 0;
	font-size: 100%;
	background: transparent;
}

.table_search th.th_title {
	background-color: #AAC4E5;
	text-align: center;
}

.table_search th.th_sub_title {
	background-color: #CAD0D6;
	text-align: center;
}

.table_search td.td_title {
	background-color: #EEF1F3;
	text-align: center;
}

.table_search td.td_btn {
	background-color: #C3C4C5;
	text-align: center;
}

.table_search td,.table_search th {
	border: #000 1px solid;
	margin: 0;
	padding: 0;
	outline: 0;
	background: transparent;
	height: 28px;
}

.arrow_l {
	border-radius: 10px;
	-moz-border-radius: 10px;
	background-color: #090808;
	-webkit-border-radius: 10px;
	border: 1px solid #080809;
	width: 80px;
	position: relative;
	padding-left: 5px;
	float: left;
	color: #909090;
	height: 20px;
	font-family: BorderWeb;
	text-align: left;
}
</style>
<script type="text/javascript">
var __BU = (function(){
	var pageArray = new Array();
	var currentPageId = null;
	var self = {};
	self.forwardTo = function(id){
		currentPageId = currentPageId || 'table_main';
		var scrollTop = document.body.scrollTop;
		document.getElementById(currentPageId).style.display = 'none';
		document.getElementById(id).style.display = '';
		setTimeout(function(){
			window.scrollTo(0, 0);
		}, 1);
		pageArray.push({
			id : currentPageId,
			scrollTop : scrollTop
		});
		currentPageId = id;
	};
	self.returnTo = function(){
		var p = pageArray.pop();
		if(!p || !currentPageId)return;
		document.getElementById(currentPageId).style.display = 'none';
		document.getElementById(p.id).style.display = '';
		window.scrollTo(0, p.scrollTop || 0);
		currentPageId = p.id;
	}
	return self;
})();
</script>
</head>
<body>
	<%
	GridViewWriter writer = new GridViewWriter(response.getWriter());
	writer.writeGridView((GridView) request.getAttribute("GridView"), true);
	%>
</body>
</html>

