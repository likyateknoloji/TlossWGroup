<html>
<head>
<title></title>
</head>
<body>
	<%
		if (session.getAttribute("LoggedIn") == "true") {
	%>
	<jsp:forward page="index.jsf" />
	<%
		} else {
			session.invalidate();
	%>
	<jsp:forward page="login.jsf" />
	<%
		}
	%>
</body>
</html>
