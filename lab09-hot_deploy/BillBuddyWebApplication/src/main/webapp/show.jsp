<%@page import="com.gs.billbuddy.model.User"%>
<%@page import="com.gs.billbuddy.model.ProcessingFee"%>
<%@page import="com.gs.billbuddy.model.Payment"%>
<%@page import="com.gs.billbuddy.model.Merchant"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.gs.billbuddy.dal.MerchantDal"%>
<%@page import="com.gs.billbuddy.dal.BillBuddyDal"%>
<%@page import="com.gs.billbuddy.dal.UserDal"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.openspaces.core.GigaSpace"%>
<%@page import="java.util.List"%>

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
		String showType = request.getParameter("type") != null ? request.getParameter("type") : "users";
		String billBuddyTopType = request.getParameter("topType");
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(new Locale("en"));
		int rowIterator = 0;
	%>
<div id="wrapper">
	<div id="main">

		<%
			if (showType.equalsIgnoreCase("billBuddy")) {
		
				BillBuddyDal billBuddyDal = BillBuddyDal.getInstance();
		%>
		<div id="topLinks">
			<ul><li><a class="first" href="show.jsp?type=billBuddy&topType=merchant">Platinum Merchants</a></li>
			<li><a href="show.jsp?type=billBuddy&topType=payment">Top Payments</a></li>
			<li><a href="show.jsp?type=billBuddy&topType=processingFee">Top Processing Fees</a></li>
			</ul>
		</div>
		<div id="infoBox">
			<%
				HashMap<String,Integer> totals = billBuddyDal.getTotalObjectCount(); 
				%>
			<p>Total Revenue amount is: $ <%=df.parse(df.format(billBuddyDal.getBillBuddyRevenue()))%></p>
			<p>Total Users quantity is: <%=df.parse(df.format(totals.get("users")))%></p>	
			<p>Total Merchants quantity is: <%=df.parse(df.format(totals.get("merchants")))%></p>
			<p>Total Payments quantity is: <%=df.parse(df.format(totals.get("payments")))%></p>
		</div>
		
			<table>
	<%
		if (billBuddyTopType!=null && billBuddyTopType.equalsIgnoreCase("merchant")) {
			List<Merchant> topMerchants = billBuddyDal.getTop5Merchants();
	%>
	<caption><%="Platinum  Merchants Report" %></caption>
	<thead>
		<tr>
			<th>NAME</th>
			<th>CATEGORY</th>
			<th>RECEIPTS</th>
			<th>FEE AMOUNT</th>
			<th>STATUS</th>
		</tr>
	</thead>
	<tbody>
		<%
			rowIterator = 0;
				for (Merchant m : topMerchants) {
		%>
		<tr class="<%=rowIterator % 2 == 0 ? "odd" : "even"%>">
			<th><a
				href='showPayments.jsp?type=merchant&id=<%=m.getMerchantAccountId()%>'>
			<%=m.getName()%></a></th>
			<td><%=m.getCategory()%></td>
			<td>$ <%=m.getReceipts()%></td>
			<td><%=df.parse(df.format(m.getFeeAmount()))%></td>
			<td><%=m.getStatus()%></td>
		</tr>
		<%
			rowIterator++;
				}
		%>
	</tbody>
	<%
		} else if (billBuddyTopType!=null && billBuddyTopType.equalsIgnoreCase("payment")) {
			List<Payment> paymentsList = billBuddyDal.getTop10Payments();
	%>
	<caption><%="Top Payments Report" %></caption>
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
				for (Payment payment : paymentsList) {
		%>
		<tr class="<%=rowIterator % 2 == 0 ? "odd" : "even"%>">
			<th><%=payment.getCreatedDate()%></th>
			<td><%=payment.getPayingAccountId()%></td>
			<td><%=payment.getReceivingMerchantId()%></td>
			<td><%=payment.getDescription()%></td>
			<td>$ <%=df.format(payment.getPaymentAmount())%></td>
			<td><%=payment.getStatus()%></td>

		</tr>
		<%
			rowIterator++;
				}
		%>
	</tbody>
	<%
		} else if (billBuddyTopType!=null && billBuddyTopType.equalsIgnoreCase("processingFee")) {
			List<ProcessingFee> processingFeeList = billBuddyDal
					.getTop10ProcessingFees();
	%>
	<caption><%="Top Processing Fees Report" %></caption>
	<thead>
		<tr>
			<th>DEPENDENT PAYMENT INFO</th>
			<th>DESCRIPTION</th>
			<th>AMOUNT</th>
			<th>STATUS</th>
			<th>CREATED DATE</th>
		</tr>
	</thead>
	<tbody>
		<%
			rowIterator = 0;
				for (ProcessingFee pf : processingFeeList) {
		%>
		<tr class="<%=rowIterator % 2 == 0 ? "odd" : "even"%>">
			<td><a
				href="showDetailedPayment.jsp?pID=<%=pf.getDependentPaymentId()%>">
			CLICK TO SEE DETAILED PAYMENT INFORMATION </a></td>
			<td><%=pf.getDescription()%></td>
			<td>$ <%=df.format(pf.getAmount())%></td>
			<td><%=pf.getStatus()%></td>
			<td><%=pf.getCreatedDate()%></td>
		</tr>
		<%
			rowIterator++;
				}
		%>
	</tbody>
	<%
		}
	%>
</table>
		<%
			
			}

			if (showType.equalsIgnoreCase("users")
					|| showType.equalsIgnoreCase("merchants")) {
		%>
		<table>
			<%
				}
				if (showType.equalsIgnoreCase("users")) {

					UserDal userDal = UserDal.getInstance();
					List<User> users = userDal.getAllUsers();
			%>
			<caption><%="Users Report" %></caption>
			<thead>
			<tr>
				<th>NAME</th>
				<th>BALANCE</th>
				<th>CREDIT LIMIT</th>
				<th>STATUS</th>
			</tr>
			</thead>
			<tbody>
			<%
				rowIterator = 0;
				for (User user : users) {
			%>
			
			<tr class="<%= rowIterator%2==0 ? "odd" : "even" %>">
				<th><a
					href='showPayments.jsp?type=user&id=<%=user.getUserAccountId()%>'>
						<%
							out.print(user.getName());
						%> </a>
				</th>
				<td>$ <%=df.parse(df.format(user.getBalance()))%></td>
				<td>$ <%=Math.abs(user.getCreditLimit())%></td>
				<td><%=user.getStatus()%></td>
			</tr>
			<%
				rowIterator++;
			}
			%>
			</tbody>
			<%
				} else if (showType.equalsIgnoreCase("merchants")) {

					MerchantDal merchantDal = MerchantDal.getInstance();
					List<Merchant> merchants = merchantDal.getAllMerchants();
			%>
			<caption><%="Merchants Report" %></caption>
			<thead>
			<tr>
				<th>NAME</th>
				<th>CATEGORY</th>
				<th>RECEIPTS</th>
				<th>FEE AMOUNT</th>
				<th>STATUS</th>
			</tr>
			</thead>
			<tbody>
			<%	
				rowIterator = 0;
				for (Merchant merchant : merchants) {
			%>
			<tr class="<%= rowIterator%2==0 ? "odd" : "even" %>">
				<th><a
					href='showPayments.jsp?type=merchant&id=<%=merchant.getMerchantAccountId()%>'>
						<%=merchant.getName()%></a></th>
				<td><%=merchant.getCategory()%></td>
				<td>$ <%=df.format(merchant.getReceipts())%></td>
				<td>$ <%=df.parse(df.format(merchant.getFeeAmount()))%></td>
				<td><%=merchant.getStatus()%></td>
			</tr>
			<%
				rowIterator++;
				}
			%>
			</tbody>
			<%
				}
			%>

		</table>
		<div>
			<%
				if(showType.equalsIgnoreCase("merchant")){
					
				}
			%>
		</div>
	</div>
	<div id="nav">
		<ul>
			<li><a href="show.jsp?type=users">Users</a></li>
			<li><a href="show.jsp?type=merchants">Merchants</a></li>
			<li><a href="show.jsp?type=billBuddy&topType=merchant">Bill Buddy</a></li>
		</ul>
	</div>
</div>
	<%@ include file="/WEB-INF/snippets/footer.jsp"%>

</body>
</html>