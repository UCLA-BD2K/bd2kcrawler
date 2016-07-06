<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Publication Crawler | BD2KCrawler</title>

<%@ include file="includes/css_includes.html" %>

</head>

<body>

	<%@ include file="includes/navigation_header.html" %>
	
	<div class="container-fluid">
          <div class="row" id="main">
          	<div class="col-sm-2" id="sidebar">
        		<ul class="list-group">
        			<li class="list-group-header">Crawler Dashboard</li>
        			<li class="list-group-item">
        				<a href="/BD2KCrawler/siteCrawler">Site Crawler</a>
        			</li>
        			<li class="list-group-item active">
        				<a href="#">Publication Crawler</a>
        			</li>
        		</ul>
          	</div>
          	<div id="search-form" class="col-sm-10 col-sm-offset-2 animated fadeIn">
          		<div class="">
          			<h1>Publication (PubMed) Crawler</h1>
          			<hr />
          			<h3 id="pub-crawler">Publication Crawler status: <span id="crawler-status-pub"></span><i class="fa fa-spinner fa-spin"></i></i></h3>
          			
          			<hr />
          			
          			<div>
          				<h3>Initiate Site Crawls</h3>
          				<div class="">
          					Crawl Publications for all centers: <br/>
          					<button type="button" class="btn btn-default" id="crawl-all-btn">Start crawler</button><span id="crawl-all-loading"></span> <br /><br />
          					<b>--- OR ---</b> <br /><br />
          					Crawl Publications for center:  
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
       				Results:
       				<div id="search-results">
       					<c:forEach items="${ results }" var="ele">
       						<h3><i class="fa fa-plus-circle"></i> [ ${ ele.getLastCrawlTime() } ]: ${ ele.getCenterID() }</h3>
       						<div>
       							<div class="panel panel-default">
       								<div class="panel-heading">
       									<div>Center: ${ ele.getCenterID() }</div>
       									<div>Last Crawl Time: ${ ele.getLastCrawlTime() }</div>
       								</div>
       								<div class="panel-body">
       									<div>Current Publications: 
       										<c:forEach items="${ ele.getFullContent() }" var="pub">
       											<p>
       												- <a href="/BD2KCrawler/digestResults?center=${ ele.getCenterID() }&pmid=${ pub.getPmid() }">(${pub.getPmid()}) ${ pub.getTitle() }</a>  
       											</p>
       											<a href="/BD2KCrawler/digestResults?center=${ ele.getCenterID() }&pmid=${ pmid }">${ pmid }</a>  
       										</c:forEach>
       									</div><br />
       									<div>Changes found since last crawl: 
											<c:forEach items="${ ele.getLastDiff() }" var="pmid">
												<a href="/BD2KCrawler/digestResults?center=${ ele.getCenterID() }&pmid=${ pmid }">${ pmid }</a>  
											</c:forEach>	
       									</div>
       								</div>
       							</div>
       						</div>
       					</c:forEach>
       				</div>
       			</div>
          	</div>
		</div>
       </div>
</body>

<%@ include file="includes/js_includes.html" %>

<script>

	$(function() {
		
		var crawlerRunning = false;	// weak guard to repeated crawl inits
				
		//periodically poll for crawler status, every 7.5 seconds
		var id = setInterval(function() {
			$('.fa-spinner').fadeIn();

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
		
		$('#search-results').accordion({"heightStyle": 'panel'});
		
		$('#crawl-all-btn').on('click', function() {
			
			// this refers to the button that was clicked
			var $btn = $(this);
			$btn.text("crawling...").addClass("disabled");
			
			if(!crawlerRunning) {
				crawlerRunning = true;	// in case client clicks button multiple times at once
				$.ajax({
					url:'/BD2KCrawler/pub/update',
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
				var center = $('#center-select option:selected').val();
				var crawlUrl = '/BD2KCrawler/pub/update';
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