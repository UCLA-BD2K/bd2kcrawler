<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>BD2KCrawler | Homepage</title>
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="resources/css/site.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<div class="container-fluid">
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
          
          <div class="container">
          	<div class="row">
          		<div class="col-sm-3"></div>
          		
          		<div class="col-sm-6">
          			<div id="login-form">
          				<h4>Login:</h4>
          				<form method="POST" action="login">
          					<label for="username">Username:</label>
          					<input class="form-control" name="username" type="text" id="username" />
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

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

</html>