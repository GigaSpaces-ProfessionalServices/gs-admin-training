<%@page import="com.gs.billbuddy.model.Merchant"%>
<%@page import="com.gs.billbuddy.model.ProcessingFee"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.List"%>
<%@page import="com.gs.billbuddy.dal.MerchantDal"%>
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
<%
	int rowIterator = 0;
	Integer merchantId = Integer.parseInt(request.getParameter("merchantID"));
	if (merchantId != null) {

		MerchantDal merchantDal = MerchantDal.getInstance();
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(new Locale("en"));
		List<ProcessingFee> merchantProcessingFeeList = merchantDal
				.getMerchantProcessingFees(new Merchant(merchantId));
%>

</head>
<body>


<%@ include file="/WEB-INF/snippets/header.jsp"%>
<div id="wrapper">
<div id="main">
<%
	if (merchantProcessingFeeList != null
				&& merchantProcessingFeeList.size() > 0) {
%>
<table>
<caption>Processing Fees collected from merchant #<%=merchantId%></caption>
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
		for (ProcessingFee pf : merchantProcessingFeeList) {
	%>
	<tr class="<%= rowIterator%2==0 ? "odd" : "even" %>">
		<td><a
			href="showDetailedPayment.jsp?pID=<%=pf.getDependentPaymentId()%>">
		CLICK TO SEE DETAILED PAYMENT INFORMATION </a></td>
		<td>
		<%=pf.getDescription()%>
		</td>
		<td>
		$ <%=df.format(pf.getAmount())%>
		</td>
		<td>
		<%=pf.getStatus()%>
		</td>
		<td>
		<%=pf.getCreatedDate()%>
		</td>
	</tr>
	<%
		rowIterator++;
	}
	%>
</tbody>
</table>

<%
	} else {
%>
<h1>NO PROCESSING FEEs WERE FOUND FOR MERCHANT #<%=merchantId%>
</h1>
<%
	}
	} else {
%>
<h1>NO MERCHANT ID WAS SUPPLIED....</h1>
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