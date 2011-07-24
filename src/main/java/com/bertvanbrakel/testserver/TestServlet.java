package com.bertvanbrakel.testserver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * I am a convenience base servlet for making it easier to create servlets for testing
 * @author Bert van Brakel
 *
 */
public abstract class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1;

    @Override
    public String getServletInfo() {
        return "AbstractMockServlet:" + getClass().getName() + "(for testing)";
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
    }
    
    @Override
    public void destroy() {           
    }
}
