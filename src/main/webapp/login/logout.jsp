<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>  
	<title>Microficha</title>
  </head>  
  <body>

	 <%@ page session="true"%>
	
	 <% 
		 session.invalidate(); 
		 request.logout();
		 
	 %>
	 <jsp:forward page="../index.html"/>

	
  </body>
</html>
