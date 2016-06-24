<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Digest for: | BD2KCrawler</title>

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
          	<div>
          		<a href="dashboard"> << Back to dashboard</a>
          	</div> 
          	<br/>
          	<div class="panel panel-default" id="digest-result-container">
          		<div class="panel-heading">Digest Results for: model.mongo_id</div>
          			
          		<div class="panel-body">
          			<div>Document ID: model.mongo_id</div>
          			<div>URL: model.url</div>
          			<div>Last Crawl Time: model.lastCrawlTimestamp</div>
          			<div>Results like in this <a target="_blank" href="https://neil.fraser.name/software/diff_match_patch/svn/trunk/demos/demo_diff.html">demo</a></div>
          		</div>
          	</div>
          		
          </div>
       </div>
</body>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</html>