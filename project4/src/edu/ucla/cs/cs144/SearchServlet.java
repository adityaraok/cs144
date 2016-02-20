package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Get the query and numResults etc
         String query = request.getParameter("q");

        String numResultsToSkip = request.getParameter("numResultsToSkip");

        String numResultsToReturn = request.getParameter("numResultsToReturn");

        // Create a search client and issue a request through the basicSearch
        AuctionSearchClient searchClient = new AuctionSearchClient();

        SearchResult[] results = searchClient.basicSearch(query, Integer.parseInt(numResultsToSkip), Integer.parseInt(numResultsToReturn));
      


        request.setAttribute("results", results);
        request.setAttribute("numResultsToSkip", Integer.parseInt(numResultsToSkip));
        request.setAttribute("query", query);

        request.setAttribute("numResultsToReturn", Integer.parseInt(numResultsToReturn));
        request.getRequestDispatcher("/searchResults.jsp").forward(request, response);

        





    }
}
