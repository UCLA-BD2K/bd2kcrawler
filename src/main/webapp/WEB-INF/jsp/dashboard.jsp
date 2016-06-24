<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dashboard | BD2KCrawler</title>

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
          </div>
         
          <div class="container">
          <div class="row">
          	<div id="search-form" class="col-sm-12">
          		<div class="">
          			<h4>Filter results below:</h4>
          			<form action="" method="GET">
          				<div class="form-group">
          					<label for="center-select">Center:</label> 
          					<select name="centerID" id="center-select">
          						<option value="all">All</option>
          						<c:forEach items="${bd2kCenters}" var="center">
          							<option value="${center}">${center}</option>
          						</c:forEach>
          					</select>
          				</div>
          				<div class="form-group">
          					<label for="center-select">Grant:</label> 
          					<select name="grant" id="center-select">
          						<option value="any">Any</option>
          						<c:forEach items="${grantList}" var="grant">
          							<option value="${grant}">${grant}</option>
          						</c:forEach>
          					</select>
          				</div>
          				<div class="form-group">
          					<label class="radio-inline">
          						<input type="radio" value="sites" name="type">Sites
          					</label>
          					<label class="radio-inline">
          						<input type="radio" value="publications' name="type">Publications
          					</label>
          					<label class="radio-inline">
          						<input type="radio" value="all" name="type" checked="checked">All
          					</label>
          				</div>
          				<div class="form-group">
          					<input type="submit" value="Search" />
          				</div>
          			</form>
          		</div>
          		<hr />
          	</div>
			</div>
			<div class="row">	
          			<div class="col-sm-12">
          				<div id="search-results">
          					Results will go here.
          				</div>
          			</div>
          		</div>
          	</div>
       </div>
  

</body>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

</html>