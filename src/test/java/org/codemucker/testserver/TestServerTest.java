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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static junitx.framework.StringAssert.assertContains;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codemucker.testserver.TestServer;
import org.codemucker.testserver.TestServlet;
import org.codemucker.testserver.capturing.CapturedRequest;
import org.junit.After;
import org.junit.Test;

public class TestServerTest {

	TestServer server = new TestServer();

	@After
	public void tearDown(){
		server.stop();
	}

	@Test
	public void ensure_simple_startup_ok() throws Exception {
		server.start();

		final int port = server.getHttpPort();
		final String host = server.getHost();

		assertTrue( "Expected port number to be greater than 0 but was " + port, port > 0 );
		assertEquals("127.0.0.1", host );
	}

	@SuppressWarnings("serial")
	@Test
	public void ensure_custom_servlets_are_invoked() throws Exception {
		final Collection<CapturedRequest> capturedRequests = new Vector<CapturedRequest>();

		server.addServlet("/my/test/path", new TestServlet() {
			@Override
			protected void doGet(final HttpServletRequest req,
					final HttpServletResponse resp) throws ServletException,
					IOException {
				capturedRequests.add(new CapturedRequest(req));
				resp.setStatus(HttpServletResponse.SC_OK);
			}
		});
		server.start();

		final long paramX = System.currentTimeMillis();
		final String url = "http://" + server.getHost() + ":" + server.getHttpPort() + "/my/test/path?x=" + paramX;

		//check we can hit the servlet, that it's run only once, and that we get the params passed to it
		final HttpClient client = new DefaultHttpClient();
		final HttpGet get = new HttpGet(url);
		final HttpResponse resp = client.execute(get);

		assertEquals(HttpServletResponse.SC_OK, resp.getStatusLine().getStatusCode());
		assertEquals(1,capturedRequests.size());
		final CapturedRequest req = capturedRequests.iterator().next();
		assertEquals(paramX + "", req.getParameters().get("x").iterator().next());
	}

	@SuppressWarnings("serial")
    @Test
    public void ensure_can_add_servlet_to_running_server() throws Exception {
        final Collection<CapturedRequest> capturedRequests = new Vector<CapturedRequest>();

        server.start();
        server.addServlet("/my/test/path2", new TestServlet() {
            @Override
            protected void doGet(final HttpServletRequest req,
                    final HttpServletResponse resp) throws ServletException,
                    IOException {
                capturedRequests.add(new CapturedRequest(req));
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        });

        final String url = "http://" + server.getHost() + ":" + server.getHttpPort() + "/my/test/path2";

        //check we can hit the servlet
        final HttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(url);
        final HttpResponse resp = client.execute(get);

        assertEquals(HttpServletResponse.SC_OK, resp.getStatusLine().getStatusCode());
        assertEquals(1,capturedRequests.size());
    }

	@Test
	public void ensure_throws_errors_when_modifying_server_properties_when_already_started() throws Exception {
		server.start();
		assertActionThrowsError(new Action() { @Override
		    public void doit() { server.setHttpPort(1234); } } );
		assertActionThrowsError(new Action() { @Override
		    public void doit() { server.setHost("localhost"); } } );
		//assertActionThrowsError(new Action() { @Override
		   // public void doit() { server.addServlet("/some/path", new TestServlet() {} ); } } );
		assertActionThrowsError(new Action() { @Override
		    public void doit() throws Exception { server.start(); } } );
	}

	private void assertActionThrowsError(final Action action) throws Exception {
		boolean thrown = false;
		try {
			action.doit();
		} catch (final IllegalStateException e){
			assertContains("wrong error msg thrown","server is already running", e.getMessage().toLowerCase() );
			thrown = true;
		}
		assertTrue("Expected server operation to of thrown an error as it was already started", thrown);
	}

	interface Action {
		void doit() throws Exception;
	}
}
