<%@page import="com.gs.billbuddy.model.Contract"%>
<%@page import="com.gs.billbuddy.dal.BillBuddyDal"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Bill Buddy</title>
	<link rel="stylesheet" type="text/css" href="css/reset.css" media="all" />
	<link rel="stylesheet" type="text/css" href="css/layout.css" media="all" />
	<link rel="stylesheet" type="text/css" href="css/styles.css" media="all" />
</head>
<body>
	<%@ include file="/WEB-INF/snippets/header.jsp"%>
<%
	Integer merchantID = new Integer(request.getParameter("mID"));
	Contract contract = BillBuddyDal.getInstance().getContract(merchantID);
	if(contract != null){
	%>
<div id="wrapper">
	<div id="main">	
		<table>
		<caption>Contract Document Details for Merchant #<%=merchantID %>:</caption>
		
		<tr class="odd">
			<td>Current transaction Fee is </td>
		</tr>
		<tr class="even">
			<td><%=contract.getTransactionPrecentFee() * 100 %>%</td>
		</tr>
		</table>
	<%
	}else{%>
		<h1>NO DOCUEMENT FOUND</h1>
		<h1><%=merchantID %> IS THE MERCHANT ID</h1>
	<%
	}
%>
	</div>
	<div id="nav">
		<ul>
			<li><a href="show.jsp?type=users">Users</a></li>
			<li><a href="show.jsp?type=merchants">Merchants</a></li>
			<li><a href="show.jsp?type=BillBuddy&topType=merchant">Bill Buddy</a></li>
		</ul>
	</div>
</div>
	<%@ include file="/WEB-INF/snippets/footer.jsp" %>

</body>
</html>