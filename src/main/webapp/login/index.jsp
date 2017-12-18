<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String errorReq = request.getParameter("error")!=null?request.getParameter("error"):"";
String msgError = "";

if("1".equals(errorReq))
	msgError = "Usuario o contraseña incorrecta";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
 <base href="<%=basePath%>">
 
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>CBR AIO</title>

<link href="login/css/bootstrap.css" rel="stylesheet">
<link href="login/css/animate.css" rel="stylesheet">
<link href="login/css/login.css" rel="stylesheet">
</head>

<body class="body-login">

<div class="container">
  <div align="center" class="animated fadeInDown"><img src="login/images/logo_grande.png" width="250" /></div>
 <p class="p-login animated fadeInUp">Ingresa al sistema AIO CBRS donde podrás acceder a todas las aplicaciones informativas del Conservador</p>
 
           <form class="form-signin fadeInUp animated" id="j_security_check" method="post" action="j_security_check" name="j_security_check">
           <h2 class="form-signin-heading">Registro</h2>
              <input class="form-control" type="text" placeholder="Usuario" id="j_username_input" name="j_username_input">
              <input type="hidden" id="j_username" name="j_username" class="form-control" />
              <input class="form-control" type="password" placeholder="Contraseña" name="j_password" onkeydown="enter(event);">
              
 			<div id="login_error" class="tooltip in " style="position:static; width: 100%;">
				<p class="text-danger bg-danger text-center"><%=msgError %></p>
			</div>
              
              <button type="button" onclick="submitform();" class="btn btn-lg btn-primary btn-block">Entrar</button>
              <div class="alert alert-error" id="msgLogin" style="display: none; margin-top:1px; margin-bottom:0px; font-size: 11px; " >                              
		      </div>
            </form>
</div>


<script src="login/js/jquery.min.js"></script>
<script src="login/js/bootstrap.js"></script>
<script>

	function enter(event){
		if(event.keyCode == 13){ //13 = ENTER
			submitform(); 
		}
	}
	
	function submitform()
	{
	  if(j_security_check.j_username_input.value.length==0) { //comprueba que no esté vacío
	    j_security_check.j_username_input.focus();   
	    //document.getElementById('error').style.display = 'block';
	    return false; //devolvemos el foco
	  }
	  if(j_security_check.j_password.value.length==0) { //comprueba que no esté vacío
	    j_security_check.j_password.focus();
	    //document.getElementById('error').style.display = 'block';
	    return false;
	  }
 		
	  var valor = document.getElementById("j_username_input").value.toLowerCase();
	  var elem = document.getElementById("j_username");

	  valor = 'CBRS\\'+valor;
	  elem.value = valor;		  	  
	  document.j_security_check.submit();
	  
	}
</script>

</body>
</html>
