<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html >
<html >
<head>
<meta charset="utf-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<meta content="telephone=no" name="format-detection" />
<meta name="apple-mobile-web-app-capable" content="yes">
<title>详情</title>
<style>
html, body, div, span,applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, dd, dl, dt, li, ol, ul, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td {margin:0;padding:0;} 
body {font-size:12px;line-height:22px;font-family:"微软雅黑","宋体","Arial Narrow";background:#5f5f5f;-webkit-text-size-adjust:none;} 
div {text-align:left;} 
address, cite, em{font-style:normal;}
li{list-style-type:none;} 
a{text-decoration:none; outline:0; cursor:pointer;} 
a:hover{text-decoration:none;} 
fieldset, img {border:0;} 
button{cursor: pointer;} 
select,input,textarea{font-size:12px;line-height:1.2em;}
.content{ width:100%; background-color:#5f5f5f; float:left;} 
.tips_info{width:100%; background-color:#f8f8f8; float:left; color: red;text-align: center;font-size:15px;} 
.info_content{ width:97%;float:left; padding-left:3%; padding-bottom:4%;} 
.info_content h3{ float:left;width:96%; margin:5% 5% 0 3%; font-size:15px; color:#fff; }
.kuang, .kuang_more{ width:97%;border-radius: 10px; -moz-border-radius: 10px; float:left; -webkit-border-radius: 10px; background-color:#fff;border:1px solid #ddd;  margin-top:3%; font-size:14px; position:relative;}
.kuang .title{ color:#909090; width:50%; float:left;}
.kuang .t_cont{ color:#323232;width:50%; float:left;}
.kuang p.line { line-height:18px;border-bottom:#ddd 1px solid; float:left;width:94%; padding:3% 3%;}
.kuang p:last-child {border-bottom:0px;}
.kuang_more .t_cont{ color:#323232;width:50%; float:left;}
.kuang_fram{position:relative; float:left;width:100%;}
.kuang_fram p{ line-height:18px;border:0px ;width:94%; float:left;padding:3% 3%;position:relative;}
.kuang_fram p:nth-child(2n) {border-bottom:1px solid #ddd;  }
.kuang_more div:last-child p:nth-last-child(2) {border-bottom:0px;}
.info_content .detail_head {position: relative;float:left;width: 96%;margin-top: 3%;}
.info_content h4{ width: 96%;float:left; text-align: center; font-size:25px; color:#fff; }
.arrow_r {width:20px; height:20px; position:absolute; right:4%; top:30%;font-size: 18px;font-weight: bolder;font-family: "宋体";color:#6f6f6f;}
.arrow_l {border-radius: 10px; -moz-border-radius: 10px; background-color:#fff; -webkit-border-radius: 10px; border:1px solid #456F9A;width:90px;position:absolute;padding-left:5px;float:left;color:#909090; height:20px; font-size: 15px;font-family: BorderWeb;}
</style> 
<script type="text/javascript">
var __BU = (function(){
	var pageArray = new Array();
	var currentPageId = null;
	var self = {};
	self.forwardTo = function(id){
		currentPageId = currentPageId || 'main';
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
<div class="content" id="main" > 
	<div class="tips_info" > 
		
	</div> 
	<div class="info_content"> 
		<div class="kuang">
		<p class="line"><span class="title">预记数额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">预算差额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">冲帐：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">付现：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">应退款：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">附件数：</span><span class="t_cont">0</span></p>
		<p class="line"><span class="title">支付方式：</span><span class="t_cont">集中结算</span></p>
		<p class="line"><span class="title">是否已经生成凭证：</span><span class="t_cont">否</span></p>
		<p class="line"><span class="title">费用支付部门：</span><span class="t_cont">财务部门</span></p>
		<p class="line"><span class="title">公司：</span><span class="t_cont">环球集团本部</span></p>
		<p class="line"><span class="title">币别：</span><span class="t_cont">人民币</span></p>
		<p class="line"><span class="title">合计金额：</span><span class="t_cont">333.00</span></p>
		<p class="line"><span class="title">核定金额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">还款金额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">合计金额副本：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">已用金额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">可用余额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">预算扣减值：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">名称：</span><span class="t_cont">报销单55</span></p>
		<p class="line"><span class="title">申请人：</span><span class="t_cont">东方成</span></p>
		<p class="line"><span class="title">职位：</span><span class="t_cont">销售总监</span></p>
		<p class="line"><span class="title">申请部门：</span><span class="t_cont">环球集团本部</span></p>
		<p class="line"><span class="title">申请日期：</span><span class="t_cont">2013-07-22</span></p>
		<p class="line"><span class="title">紧急程度：</span><span class="t_cont">低</span></p>
		<p class="line"><span class="title">单据状态：</span><span class="t_cont">已提交</span></p>
		<p class="line"><span class="title">制单日期：</span><span class="t_cont">2013-07-22</span></p>
		<p class="line"><span class="title">制单人：</span><span class="t_cont">yong</span></p>
		<p class="line"><span class="title">单据类型：</span><span class="t_cont">报销单</span></p>
		<p class="line"><span class="title">可超额控制：</span><span class="t_cont">否</span></p>
		<p class="line"><span class="title">申请人公司：</span><span class="t_cont">环球集团本部</span></p>
		<p class="line"><span class="title">来源单据类型：</span><span class="t_cont">0</span></p>
		<p class="line"><span class="title">单据编号：</span><span class="t_cont">报销单55</span></p>
		<p class="line"><span class="title">是否曾经生效：</span><span class="t_cont">否</span></p>
		<p class="line"><span class="title">创建者：</span><span class="t_cont">yong</span></p>
		<p class="line"><span class="title">创建时间：</span><span class="t_cont">2013-07-22 11:55:44</span></p>
		<p class="line"><span class="title">最后修改者：</span><span class="t_cont">yong</span></p>
		<p class="line"><span class="title">最后修改时间：</span><span class="t_cont">2013-07-22 11:55:44</span></p>
		<p class="line"><span class="title">控制单元：</span><span class="t_cont">环球国际集团</span></p>
		</div>
		<h3>分录</h3>
		<div class="kuang_more">
       	<div class="kuang_fram" onclick="__BU.forwardTo('DIV_1_0')">
		<p>
		<span class="t_cont">&nbsp;</span>
		<span class="t_cont">总经室</span>
		</p><p>
		<span class="t_cont">环球家电本部</span>
		<span class="t_cont">未处理</span>
		</p>
		<div class="arrow_r">></div>
		</div> 
		</div>
	</div> 
</div>

<div class="content" style="display: none;" id="DIV_1_0"> 
	<div class="info_content"> 
		<div class="detail_head" onclick="__BU.returnTo();">
		<span  class="arrow_l"><<&nbsp;返回单据</span>
		<h4 >分录1</h4>
		</div>
		<div class="kuang">
		<p class="line"><span class="title">业务类别：</span><span class="t_cont"></span></p>
		<p class="line"><span class="title">费用支付部门：</span><span class="t_cont">总经室</span></p>
		<p class="line"><span class="title">费用支付公司：</span><span class="t_cont">环球家电本部</span></p>
		<p class="line"><span class="title">应收状态：</span><span class="t_cont">未处理</span></p>
		<p class="line"><span class="title">应付状态：</span><span class="t_cont">未处理</span></p>
		<p class="line"><span class="title">名称：</span><span class="t_cont"></span></p>
		<p class="line"><span class="title">用途：</span><span class="t_cont"></span></p>
		<p class="line"><span class="title">费用发生时间：</span><span class="t_cont">2013-07-03</span></p>
		<p class="line"><span class="title">金额：</span><span class="t_cont">333.00</span></p>
		<p class="line"><span class="title">备注：</span><span class="t_cont"></span></p>
		<p class="line"><span class="title">参与人员：</span><span class="t_cont"></span></p>
		<p class="line"><span class="title">核定金额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">预记数额：</span><span class="t_cont"></span></p>
		<p class="line"><span class="title">预算扣减值：</span><span class="t_cont">333.00</span></p>
		<p class="line"><span class="title">已用金额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">费用类型：</span><span class="t_cont">仓储费</span></p>
		<p class="line"><span class="title">可用余额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">源单据可用余额：</span><span class="t_cont">0.00</span></p>
		<p class="line"><span class="title">使用的预算：</span><span class="t_cont">333.00</span></p>
		<p class="line"><span class="title">单据分录序列号：</span><span class="t_cont">1</span></p>
		</div>
	</div> 
</div>		

</body>
</html>

