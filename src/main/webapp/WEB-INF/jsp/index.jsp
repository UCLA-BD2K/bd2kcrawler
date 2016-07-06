<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>BD2KCrawler | Homepage</title>

<%@ include file="includes/css_includes.html" %>

</head>
<body>
	<div class="container-fluid">
		<div id="nav">
		<div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index">BD2KCrawler</a>
          </div>
          <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
              <li class="active"><a href="index">Home</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
              <li class="active"><a href="#">Login</a></li>
            </ul>
          </div>
          </div>
          
          <div class="container">
          	<div class="row">
          		<div class="col-sm-3"></div>
          		
          		<div class="col-sm-6">
          			<div id="login-form" class="animated fadeIn">
          				<h4>Login:</h4>
          				<c:if test="${ error }">
          					<div class="alert alert-danger">Invalid Credentials. Please try again.</div>
          				</c:if>
          				<c:if test="${ logout }">
          					<div class="alert alert-success">Successfully logged out.</div>
          				</c:if>
          				<form action="<c:url value="/login?${_csrf.parameterName}=${_csrf.token}"></c:url>" method="post" role="form">
          					<label for="email">Email:</label>
          					<input class="form-control" name="email" type="text" id="email" />
          					<label for="pass">Password:</label>
          					<input class="form-control" name="password" type="password" id="pass" />
          					<br />
          					<input class="form-control" type="submit" value="Login" />
          					<input class="form-control" type="reset" value="Clear" />
          				</form>
          			</div>
          		</div>
          		
          		<div class="col-sm-3"></div>
          	</div>
          </div>
	</div>
</body>

<%@ include file="includes/js_includes.html" %>

</html>