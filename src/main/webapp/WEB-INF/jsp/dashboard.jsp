<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dashboard | BD2KCrawler</title>

<%@ include file="includes/css_includes.html" %>

</head>

<body>

	<%@ include file="includes/navigation_header.html" %>
	<div class="container-fluid">
          <div class="row" id="main">
          	<div class="col-sm-2" id="sidebar">
          	<div>
          		<ul class="list-group">
          			<li class="list-group-header">Crawler Dashboard</li>
          			<li class="list-group-item">
          				<a href="/BD2KCrawler/siteCrawler">Site Crawler</a>
          			</li>
          			<li class="list-group-item"><a href="/BD2KCrawler/pubCrawler">Publication Crawler</a></li>
          		</ul>
          	</div>
          	</div>
          	<div class="col-sm-10 col-sm-offset-2">
          		<h1>Welcome to the BD2KCrawler</h1>
          	</div>
		</div>
       </div>
</body>

<%@ include file="includes/js_includes.html" %>
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