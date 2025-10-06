<%@page import="com.gs.billbuddy.model.Payment"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.gs.billbuddy.dal.BillBuddyDal"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.j_spaces.core.client.SQLQuery"%>
<%@page import="org.openspaces.core.GigaSpace"%>
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
	String paymentID = request.getParameter("pID");
	int rowIterator = 0;
	if (paymentID != null) {
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(new Locale("en"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
		Payment p = BillBuddyDal.getInstance().getPaymentById(paymentID);
%>
<div id="wrapper">
	<div id="main">
<%
		if (p == null) {
%>
		<H1>NO PAYMENT WAS FOUND FOR ID: <%=paymentID%></H1>
<%
	} else {
%>



<table>
<caption>Detailed View for Payment <br/><span>Space ID: <%=paymentID %></span></caption>
	<tbody>
		<tr class="<%=rowIterator++ % 2 == 0 ? "odd" : "even"%>">
			<td><label>CREATION DATE:</label></td>
			<td><label><%=p.getCreatedDate()%></label></td>
		</tr>
		<tr class="<%=rowIterator++ % 2 == 0 ? "odd" : "even"%>">
			<td><label>PAYING ACCOUNT ID:</label></td>
			<td><label><%=p.getPayingAccountId()%></label></td>
		</tr>
		<tr class="<%=rowIterator++ % 2 == 0 ? "odd" : "even"%>">
			<td><label>RECEIVING ACCOUNT ID:</label></td>
			<td><label><%=p.getReceivingMerchantId()%></label></td>
		</tr>
		<tr class="<%=rowIterator++ % 2 == 0 ? "odd" : "even"%>">
			<td><label>DESCRIPTION:</label></td>
			<td><label><%=p.getDescription()%></label></td>
		</tr>
		<tr class="<%=rowIterator++ % 2 == 0 ? "odd" : "even"%>">
			<td><label>PAYMENT AMOUNT:</label></td>
			<td><label>$ <%=p.getPaymentAmount()%></label></td>
		</tr>
		<tr class="<%=rowIterator++ % 2 == 0 ? "odd" : "even"%>">
			<td><label>STATUS:</label></td>
			<td><label><%=p.getStatus()%></label></td>
		</tr>
	</tbody>
</table>

<%
	}

	}
%>
		<div id="nav">
		<ul>
			<li><a href="show.jsp?type=users">Users</a></li>
			<li><a href="show.jsp?type=merchants">Merchants</a></li>
			<li><a href="show.jsp?type=BillBuddy&topType=merchant">Bill Buddy</a></li>
		</ul>
		</div>
</div>
</div>
<%@ include file="/WEB-INF/snippets/footer.jsp"%>
</body>
</html>