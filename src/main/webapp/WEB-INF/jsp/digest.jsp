<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" 
    prefix="fn" %> 
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Digest for: | BD2KCrawler</title>

<%@ include file="includes/css_includes.html" %>

</head>
<body>

	<%@ include file="includes/navigation_header.html" %>
	
	<div class="container-fluid animated fadeIn" id="main">
         
          <div class="container">
          	<div>
          		<a href="dashboard"> << Back to dashboard</a>
          	</div> 
          	<br/>
          	<div class="panel panel-default" id="digest-result-container">
          	
          		<c:if test="${ type == 'page' }">
          			<div class="panel-heading">Digest Results for record: ${ page.getUrl() }</div>
	          		<div class="panel-body">
	          			<div>Document ID: ${ page.getId() }</div>
	          			<div>URL: ${ page.getUrl() }</div>
	          			<div>Last Crawl Time: ${ page.getLastCrawlTime() }</div>
	          			<!--  <div>Results like in this <a target="_blank" href="https://neil.fraser.name/software/diff_match_patch/svn/trunk/demos/demo_diff.html">demo</a></div> -->
	          			<br/>
	          			<div>--------------------------------------------------------------</div>
	          			<br/>
	          			<div>
	          				<button id="show-content-btn">Toggle content</button>
	          				<div id="content" class="invisible" style="display:none">
	          					<br/>
	          					<%pageContext.setAttribute("linefeed", "\n"); %> 
	          					${ page.getCurrentContent().replace(linefeed, "<br />") }
	          					
	          				</div>
	          			</div><br/>
	          			<div><b>Results:</b></div> <br />
	          			<div>${ page.getLastDiff() }</div>
	          		</div>
          		</c:if>
          		
          		<c:if test="${ type == 'publication' }">
          			<div class="panel-heading">Digest Results for record: ${ publication.getPmid() }</div>
          			<div class="panel-body">
          				<div>Title: ${ publication.getTitle() }</div>
          				<div>PubMed ID: ${ publication.getPmid() }</div>
          				<div>Center: ${center}</div>
          				<div>Last Crawl Time: ${ lastCrawlTime }</div>
          				<div>Publication Date: ${ publication.getPubDate() }</div>
          				<div>Journal: ${ publication.getJournal() }</div>
          				<div>Authors: 
          					<c:forEach items="${ publication.getAuthors() }" var="author">
          						${ author }<c:if test="${ author != publication.getAuthors()[fn:length(publication.getAuthors())-1] }">, </c:if>
          					</c:forEach>
          				</div>
          			</div>
          		</c:if>
          		
       
          	</div>     		
          </div>
       </div>
</body>

<%@ include file="includes/js_includes.html" %>

<script>
	$(function() {
		$('#show-content-btn').on('click', function(e) {
			//e.preventDefault();
			if($('#content').hasClass('invisible')) {
				$('#content').fadeIn().removeClass('invisible').addClass('visible');
			}
			else {
				$('#content').fadeOut().removeClass('visible').addClass('invisible');
			}
			
		});
	});

</script>

</html>