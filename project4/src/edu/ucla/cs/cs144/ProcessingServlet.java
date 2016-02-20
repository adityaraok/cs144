package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.util.Date;

public class ProcessingServlet extends HttpServlet implements Servlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	HttpSession session = request.getSession(true);
		if (session.isNew() == true || !request.isSecure()) 
		{
			request.setAttribute("valid", "false");
			session.invalidate();
			//Bad session
		} else {
			// Get the item ID from the hidden field
			String itemid = (String) request.getParameter("purchaseid");
			// Get the item's info from session
			Item item = (Item) session.getAttribute(itemid);

			if (item != null) {
				//Good session, complete purchase
				request.setAttribute("valid", "true");

				// Pass the item to the jsp and then remove it from session so it can't be processed again
				request.setAttribute("item", item);
				session.removeAttribute(itemid);

				// Get the credit card number from the POSTed form
				String cardNumber = (String) request.getParameter("cardNumber");
				request.setAttribute("cardNumber", cardNumber);

				// Pass the time
				Date date = new Date();
				request.setAttribute("time", date.toString());
			} else {
				// Purchase isn't set in session, so we have an error
				request.setAttribute("valid", "false");
			}

		}

    	// Redirect to confirmation page
    	request.getRequestDispatcher("./confirmation.jsp").forward(request, response);
        
    }
}