<%@ page import="edu.ucla.cs.cs144.*" %>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Meet's Site</title>
     <link rel="stylesheet" type="text/css" href="./css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="./css/bootstrap-theme.min.css">
        <link rel="stylesheet" type="text/css" href="./css/main.css">
        <script src="./js/jquery1-10-1.js"></script>
        <script src="./js/bootstrap.min.js"></script>
        <script src="./js/suggest.js"></script>
        <script type="text/javascript">
            window.onload = function() {
                var field = document.getElementById("keyword-search");
                var box = document.getElementById("search-container");
                var suggest = new SuggestCtrl(field, box);
            }
        </script>
	   <style type="text/css">
		.custom {
		    width: 478px !important;
		}
		form { 
		margin: 0 auto; 
		width:250px;
		}
		#leftbutton { float: left; }
		#rightbutton { float: right; }
		</style>
	</head>

	<body>
	 <nav class="navbar navbar-default" role="navigation">
          <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="./index.html">eBay</a>
            </div>

              <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="container collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">

                    <li class="active"><a href="./keywordSearch.html">KeyWord Search</a></li>
                    <li><a href="./getItem.html">Get Item</a></li>
                </ul>
                
            </div><!-- /.navbar-collapse -->
        </nav>
        <div class="row">
        	<div class="col-lg-6 col-lg-offset-3">
		        <form class="navbar-form navbar-left"  action="./search" method="GET" role="search">
		            <div class="form-group">
		                <input id="keyword-search" class="form-control" type="text" name="q">
		                <input class="form-control" name="numResultsToSkip" type="hidden" value="0">
		                <input class="form-control" name="numResultsToReturn" type="hidden" value="10">

		            </div>
		            <button type="submit" class="btn btn-default">Search Item</button>
		            <div id="search-container"></div>
		        </form>
		    </div>
	    </div>
        
		<!--<form action="./search" method="GET">
		Search:  <input name="q" type="text"/>
		      	 <input name="numResultsToSkip" type="hidden" value="0" />
		      	 <input name="numResultsToReturn" type="hidden" value="10" />
		</form>
		-->
		<div class="row">
            <div class="col-lg-8 col-lg-offset-2">
		<div class="page-header">
  <h1>SEARCH RESULTS</h1>
</div>
 <div class="panel panel-default">
                                <div class="panel-heading">Search Results</div>
                                <div class="panel-body">
		<ul>
		<%         String query = (String) request.getAttribute("query");
				   int numSkip = Integer.parseInt(request.getAttribute("numResultsToSkip").toString());
			       int numReturn = Integer.parseInt(request.getAttribute("numResultsToReturn").toString());
%>


		<%
		
        //String query = (String) request.getAttribute("query");
  		if (numSkip > 0)
        {
                %> <a href="/eBay/search?q=<%= query %>&numResultsToSkip=<%= numSkip-10 %>&numResultsToReturn=<%= numReturn %>">Previous Page</a>
                <%
        }
        
        SearchResult[] results = (SearchResult[]) request.getAttribute("results");
        
        if (results.length - 10 >= 0)
        {
                %> <a href="/eBay/search?q=<%= query %>&numResultsToSkip=<%= numSkip+10 %>&numResultsToReturn=<%= numReturn %>">Next Page</a>
                <%
        }
        %>
		

<br>
		<% 
				
			for(int i = 0; i < results.length; i++) { %>
			<!--<li><a href="/eBay/item?id=<%= results[i].getItemId()%>"><%= results[i].getName() %></a></li>-->
      		<% if(i%2==0 || i==0)
      		{

      		%>
			<button class="btn btn-large btn-info custom" id='leftbutton' type="button" value="Page" onclick="location.href='/eBay/item?id=<%= results[i].getItemId()%>';"><%= results[i].getName() %></button>
			
			<%
			} 
			else 
			{ %>

			
			
			<button class="btn btn-large btn-info custom" id='rightbutton' type="button" value="Page" onclick="location.href='/eBay/item?id=<%= results[i].getItemId()%>';"><%= results[i].getName() %></button>
			<br>
			<br>
			<% 
			}%>

			<%
			} %>
		</ul>
		</div>
		</div>
		</div>
 </div>
        </div>

	</body>
</html>
