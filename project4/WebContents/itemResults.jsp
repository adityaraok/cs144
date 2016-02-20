<%@ page import="edu.ucla.cs.cs144.*" %>
<%@ page import="java.util.*" %>
<% 
    Item item = (Item) request.getAttribute("item");
    User seller = (User) request.getAttribute("seller");
    SortedSet catSet =  (SortedSet) request.getAttribute("categories");
    Iterator categories = catSet.iterator();
    Vector<Bid> bidVec =  (Vector) request.getAttribute("bids");
    Iterator bids = bidVec.iterator();
%>
<!DOCTYPE html>
<html>
    <head>
        <title><%= item.i_name %></title>
        <link rel="stylesheet" type="text/css" href="./css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="./css/bootstrap-theme.min.css">
        <link rel="stylesheet" type="text/css" href="./css/main.css">
        <script src="./js/jquery1-10-1.js"></script>
        <script src="./js/bootstrap.min.js"></script>
        <script src="./js/suggest.js"></script>
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
        <script type="text/javascript">
            var geocoder;

            function initialize() {
                geocoder = new google.maps.Geocoder();
                var latlng = new google.maps.LatLng(0.0, 0.0);
                var myOptions = { 
                                    zoom: 4, // default is 8  
                                    center: latlng, 
                                    mapTypeId: google.maps.MapTypeId.ROADMAP 
                                  }; 

                var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        
                var address = "<%= seller.getLocation() %>, <%= seller.getCountry() %>";
                geocoder.geocode(
                    {'address': address},
                    function(results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            map.setCenter(results[0].geometry.location);
                            map.fitBounds(results[0].geometry.viewport);
                            map.setZoom(8);
                            var marker = new google.maps.Marker({
                                map: map,
                                position: results[0].geometry.location});
                        } else {
                            geocoder.geocode( { 'address': "<%= "USA" %>"},
                            function(results, status) {
                                if (status == google.maps.GeocoderStatus.OK)
                                {
                                    map.setCenter(results[0].geometry.location);
                                    map.fitBounds(results[0].geometry.viewport);
                                    var marker = new google.maps.Marker({
                                        map: map,
                                        position: results[0].geometry.location});
                                }
                            })
                        }
                    }
                );
            }
        </script>
    </head>

    <body onload="initialize()">
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
                    <li><a href="./keywordSearch.html">Keyword Search</a></li>
                    <li><a href="./getItem.html">Get Item</a></li>
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
            <div class="col-lg-10 col-lg-offset-1">
                <div class="page-header">
                    <h1><%= item.i_name %></h1>
                    <ul class="list-inline">
                        <% while (categories.hasNext()) { %>
                            <li><span class="label label-default"><%= categories.next().toString() %></span></li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-10 col-lg-offset-1">
                <div class="col-lg-7">  
                    <div class="col-lg-4">
                        <div class="row">
                            <div class="panel panel-default">
                                <div class="panel-heading">Auction Info</div>
                                <div class="panel-body">
                                    <dl>
                                        <dt>Current Price</dt>
                                            <dd>$<%= item.i_currently %></dd>
                                        <dt>Number of Bids</dt>
                                                <dd><%= item.i_numberofbids %></dd>
                                        <% if(item.i_buyprice.length() != 0) { %>
                                            <dt>Buy Price</dt>
                                                <dd>$<%= item.i_buyprice %> <a class="button btn-sm btn-primary pull-right" href="./payment?id=<%=item.i_id %>">Buy Now</a></dd>
                                        <% } %>
                                        <dt>First Bid</dt>
                                            <dd>$<%= item.i_firstbid %></dd>
                                        <dt>Start Date</dt>
                                            <dd><%= item.i_started %></dd>
                                        <dt>End Date</dt>
                                            <dd><%= item.i_ends %></dd>
                                    </dl>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="panel panel-default">
                                <div class="panel-heading">Seller Info</div>
                                <div class="panel-body">
                                    <dl>
                                        <dt>Seller</dt>
                                        <dd><%= seller.u_name %></dd>
                                        <dt>Rating</dt>
                                        <dd><%= seller.u_rating %></dd>
                                        <dt>Location</dt>
                                        <dd><%= seller.u_location + ", " + seller.u_country %></dd>
                                    </dl>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-8">
                        <div class="panel panel-default">
                            <div class="panel-body">
                                <p><%= item.i_description %></p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-5">
                    <div id="map_canvas" style="width: 100%; height: 400px"></div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-6 col-lg-offset-3">
                <h3>Bidding History</h3>
                <% if (bids.hasNext()) { %>
                    <table class="table">
                        <tr>
                            <th>#</th>
                            <th>Bidder</th>
                            <th>Time</th>
                            <th>Amount</th>
                        </tr>

                        <% int i=0;
                           while (bids.hasNext()) { 
                                Bid current = (Bid) bids.next();
                                i++; %>
                                <tr><td><%= i %></td><td><%= current.b_userid %></td><td><%= current.b_time %></td><td><%= "$" + current.b_amount %></td></tr>
                        <% } %> 
                    </table>
                <% } else { %>
                    <div class="col-lg-12">
                        <div class="alert alert-info">
                            <p class="text-center">No bids to display</p>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
    </body>
</html>
