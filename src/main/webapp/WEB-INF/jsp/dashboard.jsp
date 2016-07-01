<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dashboard | BD2KCrawler</title>

<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
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
            	<li><a href="#" onclick="$('#logout-form').submit()">Logout (${ user })</i></a></li>
            </ul>
          </div>
         
          <div class="container">
          <form action="<c:url value="/logout"></c:url>" method="post" class="hidden" id="logout-form">
    		<input type="submit" value="Logout"/>
    		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		</form>
          <div class="row">
          	<div id="search-form" class="col-sm-12">
          		<div class="">
          			<h3 id="site-crawler">Site Crawler status: <span id="crawler-status"></span><i class="fa fa-spinner fa-spin"></i></i></h3>
          			<h3 id="pub-crawler">Publication Crawler status: <span id="crawler-status-pub"></span><i class="fa fa-spinner fa-spin"></i></i></h3>
          			<h4>Filter results below:</h4>
          			<div class="form-group">
          				<c:choose>
          					<c:when test='${ type.equals("sites") == true }'>
          						<label class="radio-inline">
          							<input type="radio" value="sites" name="type" checked="checked">Sites
          						</label>
          						<label class="radio-inline">
          							<input type="radio" value="publications" name="type">Publications
          						</label>
          					</c:when>
          					<c:otherwise>
          						<label class="radio-inline">
          							<input type="radio" value="sites" name="type">Sites
          						</label>
          						<label class="radio-inline">
          							<input type="radio" value="publications" name="type" checked="checked">Publications
          						</label>
          					</c:otherwise>
          				</c:choose>
          					
          			</div>
          			<form action="" method="GET" id="dashboard-form">
          				<div class="form-group">
          					<label for="center-select">Center:</label> 
          					<select name="center" id="center-select">
          						<option value="all">All</option>
          						<c:forEach items="${bd2kCenters}" var="center">
          							<c:choose>
          								<c:when test="${ center.equals(chosenCenter) }">
          									<option value="${center}" selected="selected">${center}</option>
          								</c:when>
          								<c:otherwise>
          									<option value="${center}">${center}</option>
          								</c:otherwise>
          							</c:choose>
          						</c:forEach>
          					</select>
          				</div>
          				<!--  <div class="form-group" style="display:none">
          					<label for="center-select">Grant:</label> 
          					<select name="grant" id="center-select">
          						<option value="any">Any</option>
          						<c:forEach items="${grantList}" var="grant">
          							<option value="${grant}">${grant}</option>
          						</c:forEach>
          					</select>
          				</div>-->
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
          				Results (Showing max 20):
          				<div id="search-results">
          					<ul class="list-group">
          						<c:forEach items="${ results }" var="ele">
          							<li class='list-group-item'>
          								<a href="/BD2KCrawler/digestResults?id=${ ele.getId() }">
          									[ ${ ele.getLastCrawlTime() } ]: ${ ele.getUrl() }
          								</a>
          							</li>
          						</c:forEach>
          					</ul>
          				</div>
          			</div>
          		</div>
          		<div class="text-center">
          			<!-- pagination -->
       				<nav>
 		 				<ul class="pagination">
 		 					<c:choose>
 		 					<c:when test="${pageNum-1 > 0}">
 		 						<li class=""><a href="dashboard?type=${ type }&center=${ chosenCenter }&page=${pageNum-1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
 		 					</c:when>
    						<c:otherwise>
    							<li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
    						</c:otherwise>
    						</c:choose>
    						<li class="active"><a href="#">${pageNum} <span class="sr-only">(current)</span></a></li>
    						<li><a href="dashboard?type=${ type }&center=${ chosenCenter }&page=${pageNum+1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
  						</ul>
					</nav>
          		</div>
          	</div>
       </div>
</body>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script>

	//probably want to use some templating JS library like backbone
	//deprecated --  no longer used in favor of server side rendering
	function generateListHTML(objects) {
		var html = "<ul class-'list-group'>";
		for(var i = 0; i < objects.length; i++) {		
			html += 
				"<li class='list-group-item'>" + 
					"<a href='/BD2KCrawler/digestResults?id=" + objects[i].id + "'>" +
						("[" + objects[i].lastCrawlTime + "]: " + objects[i].url) + 
					"</a>" +
				"</li>";
		}
		
		html+= "</ul>";
		return html;
	}

	$(function() {
				
		//periodically poll for crawler status, every 7.5 seconds
		var id = setInterval(function() {
			$('.fa-spinner').fadeIn();
			$.ajax({
				url:"/BD2KCrawler/news/crawlerStatus",
				success:function(res) {
					$('#site-crawler .fa-spinner').fadeOut();
					$('#crawler-status').html(res);
				},
				error:function() {
					console.log("error in getting crawler status");
				}
			});
			
			$.ajax({
				url:"/BD2KCrawler/pub/crawlerStatus",
				success:function(res) {
					$('#pub-crawler .fa-spinner').fadeOut();
					$('#crawler-status-pub').html(res);
				},
				error:function() {
					console.log("error in getting pub crawler status");
				}
			})
			
		}, 7500);
		
		//clearInterval(id);	//temp for dev
	});
	
	
</script>

</html>