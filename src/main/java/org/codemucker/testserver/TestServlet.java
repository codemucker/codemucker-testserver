/*
 * Copyright 2011 Bert van Brakel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codemucker.testserver;

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
