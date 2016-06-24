<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Page</title>
<style>
.section {
	width:100%;
	padding:50px;
}
</style>
</head>
<body>

	<div>
		<h1>Test page, hit links below to test certain API endpoints</h1>
		<h2>By default, a ping will be sent to service and model to test functionality.</h2>
		
		<div>
			<div class="section">
				<div id="crawl-url">
					<h3>Crawl individual Site based on ID/URL</h3>
					<form method="GET" id="crawl-url-form">
						<input id="crawl-url-input" type="text" name="url" />
						<input type="submit" value="Crawl URL" />
					</form>
				</div>
			</div>
			<div class="section">
				<div id="crawl-site">
					<h3>TODO: crawl an entire site</h3>
				</div>
			</div>
			<a href="index">dummy link for crawler</a>
		</div>
	</div>

</body>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<script type="text/javascript">
	$(function() {
		$('#crawl-url-form').on("submit", function(e) {
			e.preventDefault();
			console.log('submit pressed');
		
			$.ajax({
				url:'news/update/' + $('#crawl-url-input').val(), //probably should sanitize in final
				method:'GET',
				success:function(data) {
					console.log('success');
				},
				error:function(xhr, ajaxOptions, errorThrown) {
					console.log("error occured " + errorThrown);
				}
			})
		});
	});
</script>
</html>