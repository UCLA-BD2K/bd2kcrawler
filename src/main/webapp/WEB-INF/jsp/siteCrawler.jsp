<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Site Crawler | BD2KCrawler</title>

<%@ include file="includes/css_includes.html" %>

</head>

<body>

	<%@ include file="includes/navigation_header.html" %>
	
	<div class="container-fluid">
          <div class="row" id="main">
          	<div class="col-sm-2" id="sidebar">
        		<ul class="list-group">
        			<li class="list-group-header">Crawler Dashboard</li>
        			<li class="list-group-item active">
        				<a href="#">Site Crawler</a>
        			</li>
        			<li class="list-group-item">
        				<a href="/BD2KCrawler/pubCrawler">Publication Crawler</a>
        			</li>
        		</ul>
          	</div>
          	<div id="search-form" class="col-sm-10 col-sm-offset-2 animated fadeIn">
          		<div class="">
          			<h1>Site Crawler</h1>
          			<hr />
          			<h3 id="site-crawler">Status: <span id="crawler-status"></span><i class="fa fa-spinner fa-spin"></i></i></h3>
          			
          			<hr />
          			
          			<div>
          				<h3>Initiate Site Crawls</h3>
          				<div class="">
          					Crawl all centers: <br/>
          					<button type="button" class="btn btn-default" id="crawl-all-btn">Start crawler</button><span id="crawl-all-loading"></span> <br /><br />
          					<b>--- OR ---</b> <br /><br />
          					Crawl center:  
          					<select name="center" id="crawl-center-select">
          						<option value="all">All</option>
          						<c:forEach items="${bd2kCenters}" var="center">	
          							<option value="${center}">${center}</option>
          						</c:forEach>
          					</select> <br /> <br/>
          					<button type="button" class="btn btn-default" id="crawl-center-btn">Start crawler</button><span id="crawl-center-loading"></span>
          				</div>
          			</div>
          			
          			<hr />
          			
          			<h3>View crawl results:</h3>
          			
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
          				<div class="form-group">
          					<input type="submit" value="Search" />
          				</div>
          			</form>
          		</div>
          		<hr />
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
          		<div class="text-center">
          			<!-- pagination -->
       				<nav>
 		 				<ul class="pagination">
 		 					<c:choose>
 		 					<c:when test="${pageNum-1 > 0}">
 		 						<li class=""><a href="siteCrawler?center=${ chosenCenter }&page=${pageNum-1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
 		 					</c:when>
    						<c:otherwise>
    							<li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
    						</c:otherwise>
    						</c:choose>
    						<li class="active"><a href="#">${pageNum} <span class="sr-only">(current)</span></a></li>
    						<li><a href="siteCrawler?center=${ chosenCenter }&page=${pageNum+1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
  						</ul>
					</nav>
          		</div>
          	</div>
		</div>
       </div>
</body>

<%@ include file="includes/js_includes.html" %>

<script type="text/javascript">

	$(function() {
		
		var crawlerRunning = false;
		
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
		}, 7500);
		
		$('#crawl-all-btn').on('click', function() {
			
			// this refers to the button that was clicked
			var $btn = $(this);
			$btn.text("crawling...").addClass("disabled");
			
			if(!crawlerRunning) {
				crawlerRunning = true;	// in case client clicks button multiple times at once
				$.ajax({
					url:'/BD2KCrawler/news/update',
					success:function(d) {
						$btn.text("Start crawler").removeClass("disabled");
						crawlerRunning = false;
					},
					error:function() {
						$btn.text("Error during crawl").removeClass("disabled");
						crawlerRunning = false;
					}
				});
			}
			else {
				console.log("Crawler already running");
			}
			
		});
		
		$('#crawl-center-btn').on('click', function() {
			
			// this refers to the button that was clicked
			var $btn = $(this);
			$btn.text("crawling...").addClass("disabled");
			
			if(!crawlerRunning) {
				
				crawlerRunning = true;
				var center = $('#crawl-center-select option:selected').val();
				var crawlUrl = '/BD2KCrawler/news/update';
				if(center != "all") {
					crawlUrl += ('/' + center);
				}
				$.ajax({
					url: crawlUrl,
					success:function(d) {
						$btn.text("Start crawler").removeClass("disabled");
						crawlerRunning = false;
					},
					error:function() {
						$btn.text("Error during crawl").removeClass("disabled");
						crawlerRunning = false;
					}
				}); 
			}
			else {
				console.log("Crawler already running");
			}
			
		});
		
		
	});
	
	
</script>

</html>