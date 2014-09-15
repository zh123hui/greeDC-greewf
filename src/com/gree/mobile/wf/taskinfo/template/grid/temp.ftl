<!DOCTYPE html >
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<meta content="telephone=no" name="format-detection" />
<meta name="apple-mobile-web-app-capable" content="yes">
<title></title>
<style>
body{
    width: 96%;
}
.view {width: 99%;clear: both;border: #d2d0d0 1px solid;border-collapse: collapse;border-spacing: 0;
}
.rotate90{height: 99%;-webkit-transform: rotate(90deg);-moz-transform: rotate(90deg);filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=1);
}

.view tr,.view tbody {
	margin: 0;
	padding: 0;
	border: 0;
	outline: 0;
	font-size: 100%;
	background: transparent;
}

.view th.th_title {
	background-color: #AAC4E5;
	text-align: center;
}

.view th.th_sub_title {
	background-color: #CAD0D6;
	text-align: center;
}

.view td.td_title {
	background-color: #EEF1F3;
	text-align: center;
}

.view td.td_btn {
	background-color: #C3C4C5;
	text-align: center;
}

.view td,.view th {
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
	var pArr = [];
	var cpi = null;
	var self = {};
	self.forwardTo = function(id){
		cpi = cpi || '${mainViewId}';
		var scrollTop = document.body.scrollTop;
		document.getElementById(cpi).style.display = 'none';
		document.getElementById(id).style.display = '';
		setTimeout(function(){
			window.scrollTo(0, 0);
		}, 1);
		pArr.push({
			id : cpi,
			scrollTop : scrollTop
		});
		cpi = id;
	};
	self.returnTo = function(){
		var p = pArr.pop();
		if(!p || !cpi)return;
		document.getElementById(cpi).style.display = 'none';
		document.getElementById(p.id).style.display = '';
		window.scrollTo(0, p.scrollTop || 0);
		cpi = p.id;
	}
	return self;
})();
</script>
</head>
<body>
	${content}
</body>
</html>

