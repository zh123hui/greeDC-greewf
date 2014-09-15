<!DOCTYPE html >
<html >
<head>
<meta charset="utf-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<meta content="telephone=no" name="format-detection" />
<meta name="apple-mobile-web-app-capable" content="yes">
<title>list</title>
<style>
html, body, div, span,applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, dd, dl, dt, li, ol, ul, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td {margin:0;padding:0;} 
body {font-size:12px;line-height:22px;font-family:"微软雅黑","宋体","Arial Narrow";background:#FFFFFF;-webkit-text-size-adjust:none;} 
div {text-align:left;} 
address, cite, em{font-style:normal;}
li{list-style-type:none;} 
a{text-decoration:none; outline:0; cursor:pointer;} 
a:hover{text-decoration:none;} 
fieldset, img {border:0;} 
button{cursor: pointer;} 
select,input,textarea{font-size:12px;line-height:1.2em;}
.content{ width:100%; background-color:#FFFFFF; float:left;} 
.tips_info{width:100%; background-color:#f8f8f8; float:left; color: red;text-align: center;font-size:15px;}
.info_content{ width:97%;float:left; padding-left:3%; padding-bottom:4%;} 
.kuang{ width:97%;  float:left; background-color:#fff;border:1px solid #ddd;  margin-top:3%; font-size:14px; position:relative;}
.kuang p{ line-height:18px;width:94%; float:left;} 
.kuang .line{ border-bottom:#ddd 1px solid; float:left;width:94%; padding:3% 3%;}
.kuang .line_s{ float:left;width:94%; padding:3% 3%;}
.kuang .title{ color:#909090; width:45%; float:left;}
.kuang .t_cont{ color:#323232;width:55%; float:left;}
.kuang .noline{ float:left;width:94%; padding:3% 3%;}
.info_content h3{ float:left;width:96%; margin:5% 5% 0 3%; font-size:15px; color:#fff; }
</style> 
</head>
<body> 
<div class="content" id="first" > 
	<div class="info_content"> 
	
	

<#list data.groupItems as item>

<#if item.title?has_content>
		<h3>${item.title}</h3>
</#if>		
		<div class="kuang">
<#list item.items as prop>
<#if prop_has_next>
		<p class="line"><span class="title">${prop.title}：</span><span class="t_cont">${prop.value?if_exists}</span></p>
<#else>
		<p class="line_s"><span class="title">${prop.title}：</span><span class="t_cont">${prop.value?if_exists}</span></p>
</#if>
</#list>
		</div>
</#list>	


<#list data.listItems as item>

<#list item.items as values>
		<h3>${item.title}${values_index+1}</h3>
		<div class="kuang">
<#list values.items as prop>
<#if prop_has_next>
		<p class="line"><span class="title">${prop.title}：</span><span class="t_cont">${prop.value?if_exists}</span></p>
<#else>
		<p class="line_s"><span class="title">${prop.title}：</span><span class="t_cont">${prop.value?if_exists}</span></p>
</#if>
</#list>
		</div>
</#list>

</#list>	
	</div> 
</div>
</body>
</html>
