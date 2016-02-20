<%@ page import="edu.ucla.cs.cs144.*" %>

<!DOCTYPE html>
<html>  
    <head>  
        <link rel="stylesheet" type="text/css" href="./css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="./css/bootstrap-theme.min.css">
        <link rel="stylesheet" type="text/css" href="./css/main.css">
        <script src="./js/jquery1-10-1.js"></script>
        <script src="./js/bootstrap.min.js"></script>       
        <title>Purchase Confirmation</title> 
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
                <a class="navbar-brand" href="http://<%= request.getServerName() %>:8080<%= request.getContextPath() %>/index.html">eBay</a>
            </div>

              <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="container collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li><a href="http://<%= request.getServerName() %>:8080<%= request.getContextPath() %>/keywordSearch.html">Keyword Search</a></li>
                    <li><a href="http://<%= request.getServerName() %>:8080<%= request.getContextPath() %>/getItem.html">Get Item</a></li>
                </ul>
                <form class="navbar-form navbar-left" action="./item" method="GET" role="search">
                    <div class="form-group">
                        <input class="form-control" type="text" name="id">
                    </div>
                    <button type="submit" class="btn btn-default">Go</button>
                </form>
            </div><!-- /.navbar-collapse -->
        </nav>
        
        <div class="row">
            <% if (request.getAttribute("valid").equals("false")) { %>
                <div class="col-lg-6 col-lg-offset-3">
                    <div class="alert alert-danger">
                        <p class="text-center">Invalid request</p>
                    </div>
                </div>


            <% } else { %>
                <% Item item = (Item) request.getAttribute("item"); 
                	String cardNumber = (String) request.getAttribute("cardNumber");
                	String time = (String) request.getAttribute("time"); %>

	            <div class="col-lg-6 col-lg-offset-3">
	                <div class="alert alert-success">
	                    <p class="text-center">Purchase Successful</p>
	                </div>
	            </div>
            
                <div class="col-lg-8 col-lg-offset-2">
                    <div class="well">
                        <h2 class="text-center">Payment Confirmation</h2>

                        <div class="row">
                            <div class="col-lg-6 col-lg-offset-3">
                                <dl class="dl-horizontal">
                                    <dt>Item Number</dt>
                                            <dd> <%= item.i_id %> </dd>
                                    <dt>Item Name</dt>
                                            <dd> <%= item.i_name %> </dd>
                                    <dt>Payment Amount</dt>
                                        <dd> $<%= item.i_buyprice %> </dd>
                                    <dt>Card Number</dt>
                                    	<dd> <%= cardNumber %></dd>
                                    <dt>Posted</dt>
                                    	<dd> <%= time %></dd>
                                </dl>
                        
                            </div>
                        </div>

                        <br>
                        <br>
                    </div>
                </div>
                
            <% } %>

        </div>
    </body> 
</html>
