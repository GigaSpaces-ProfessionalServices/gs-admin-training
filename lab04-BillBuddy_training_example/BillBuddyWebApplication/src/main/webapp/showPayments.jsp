<%@page import="com.gs.billbuddy.model.Merchant"%>
<%@page import="com.gs.billbuddy.model.User"%>
<%@page import="com.gs.billbuddy.model.Payment"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.List"%>
<%@page import="com.gs.billbuddy.dal.MerchantDal"%>
<%@page import="com.gs.billbuddy.dal.UserDal"%>

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
		
		String type = request.getParameter("type");
		String  typeFirstCharUpperCase = new String(type.charAt(0)+"").toUpperCase()+type.substring(1);
		
		Integer id = Integer.parseInt(request.getParameter("id"));
		int rowIterator = 0 ;
		
		List<Payment> payments = null;
		UserDal userDal = null;
		MerchantDal merchantDal = null;
		if (type.equalsIgnoreCase("user")) {
			userDal = UserDal.getInstance();
			payments = userDal.getUserPayments(new User(id));
		} else if (type.equalsIgnoreCase("merchant")) {
			merchantDal = MerchantDal.getInstance();
			payments = merchantDal.getMerchantPayments(new Merchant(id));
		}
	%>
<div id="wrapper">
	<div id="main">
		<%
			if (payments != null && payments.size() > 0) {
				DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(new Locale("en"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
		%>
		
			<%
				if (type.equalsIgnoreCase("merchant")) {
			%>
			<div id="topLinks">
			<ul>
			<li><a class="first" href='showProcessingFee.jsp?merchantID=<%=id%>'>show
				processing fees</a></li>
			<li><a href='showContractDocument.jsp?mID=<%=id%>'>show
				Merchant's Contract Docuemnt</a></li>
				</ul>
				</div>
			<%
				}
			%>
		
		<table>
		<caption><%=typeFirstCharUpperCase+" Payments Report" %></caption>
		<thead>
			<tr>
				<th>CREATION DATE</th>
				<th>PAYING ACCOUNT</th>
				<th>RECIEVING ACCOUNT</th>
				<th>DESCRIPTION</th>
				<th>PAYMENT AMOUNT</th>
				<th>STATUS</th>
			</tr>
		</thead>
		<tbody>
			<%
				rowIterator = 0;
				for (Payment payment : payments) {
			%>
			<tr class="<%= rowIterator%2==0 ? "odd" : "even" %>">
				<td>
					<%=sdf.format(payment.getCreatedDate()).toString()%>
				</td>
				<td>
					<%=payment.getPayingAccountId()%>
				</td>
				<td>
					<%=payment.getReceivingMerchantId()%>
				</td>
				<td>
					<%=payment.getDescription()%>
				</td>
				<td>
					$ <%=df.format(payment.getPaymentAmount())%>
				</td>
				<td>
					<%=payment.getStatus()%>
				</td>

			</tr>
			<%
				rowIterator++;
			}
			%>
			</tbody>
		</table>
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
	<%@ include file="/WEB-INF/snippets/footer.jsp"%>
</body>
</html>