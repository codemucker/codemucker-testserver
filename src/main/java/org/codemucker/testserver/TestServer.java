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

import static org.codemucker.lang.Check.checkNotBlank;
import static org.codemucker.lang.Check.checkNotEmpty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
/**
 * I make it easy to startup a server and mock out http responses. This is useful when
 * testing web clients
 *
 * @author Bert van Brakel
 *
 */
public class TestServer implements org.codemucker.testserver.Server {

	private static final Logger LOG = Logger.getLogger(TestServer.class);

	/**
	 * The servlets we want to register when starting up the server
	 */
	private final Map<String, Servlet> servletsToRegisterOnStartup = new LinkedHashMap<String, Servlet>();

	private Server server;
	/**
	 * The host ot use. Null means use localhost
	 */
	private String host = "127.0.0.1";
	/**
	 * The port to use. Zero or less means find an available port
	 */
	private int httpPort = 0;

	@Override
    public void start() throws Exception {
		LOG.debug("starting jetty mock server");
		checkNotRunning();
		server = new Server();
		if (httpPort <= 0) {
			httpPort = findFreePort(host);
		}
		// set the channel we are listenting through
		final SelectChannelConnector con = new SelectChannelConnector();
		con.setPort(httpPort);
		con.setHost(host);
		server.addConnector(con);
		// set up the handler which will matching incoming requests to
		// configured paths
		final ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.NO_SESSIONS
						| ServletContextHandler.NO_SECURITY);
		context.setContextPath("/");
		server.setHandler(context);

		// add the servlets to the servlet handler
		for (final String path : servletsToRegisterOnStartup.keySet()) {
			context.addServlet(new ServletHolder(servletsToRegisterOnStartup.get(path)), path);
		}
		// start the server! Will not block once the server is up
		server.start();
	}

	public static int findFreePortOnLocalhost() {
		try {
			return findFreePort("127.0.0.1");
		} catch (final UnknownHostException e) {
			// shouldn't usually happen. Turn into a runtime exception as we
			// don't want to clutter tests
			throw new RuntimeException("Error finding localhost", e);
		}
	}

	public static int findFreePort(String hostNameOrIP) throws UnknownHostException {
		if (hostNameOrIP != null && hostNameOrIP.trim().length() == 0) {
			hostNameOrIP = null;
		}
		ServerSocket sock = null;
		try {
			// only need a single connection to find a free port
			sock = new ServerSocket(0, 1, hostNameOrIP==null?null:InetAddress.getByName(hostNameOrIP));
			// free this port up pronto when we close it
			sock.setReuseAddress(true);
			return sock.getLocalPort();
		} catch (final IOException e) {
			throw new RuntimeException("Can't free port for host " + hostNameOrIP
					+ "(null means to find any local host)", e);
		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (final IOException e) {
					// ignore, we don't really care
				}
			}
		}
	}

	@Override
    public void stop() {
		if (server != null) {
			try {
				LOG.debug("stopping jetty mock server");
				server.stop();
			} catch (final Exception e) {
				LOG.warn("Unable to stop the server", e);
			} finally {
				server = null;
			}
		}
	}

	private boolean isServerRunning() {
	    return server !=null;
	}

	private void checkNotRunning() {
		if (isServerRunning()) {
			throw new IllegalStateException(
					"test server is already running. Stop server first before trying to modify or start it");
		}
	}

    public void addServlet(final String path, final Servlet servlet) {
        String p = StringUtils.trimToNull(path);
        checkNotEmpty("path", p);
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Adding mock servlet with path '%s'", p));
        }
        if (isServerRunning()) {
            ((ServletContextHandler) server.getHandler()).addServlet(
                    new ServletHolder(servlet), p);
        }

        // save the servlet for later if we decide to restart the server, or the
        // server has not yet been started
        servletsToRegisterOnStartup.put(p, servlet);
    }

	public void setHttpPort(final int port) {
		checkNotRunning();
		this.httpPort = port;
	}

	public void setHost(final String host) {
		checkNotRunning();
		checkNotBlank("host", host);
		this.host = host;
	}

	@Override
    public String getHost() {
		return host;
	}

	/**
	 * Return the port the server is running on, or 0 if the server is not yet running
	 * @return
	 */
	@Override
    public int getHttpPort() {
		return httpPort;
	}

	public String getBaseHttpUrl(){
	    if( httpPort <= 0){
	        throw new IllegalStateException( "port not manually set and server not started (auto find a free port), so don't know the port number yet" );
	    }
		return "http://" + host + ":" + httpPort;
	}
	
	/**
	 * Return the underlying jetty server
	 */
	public Server getJettyServer(){
		return server;
	}
}
