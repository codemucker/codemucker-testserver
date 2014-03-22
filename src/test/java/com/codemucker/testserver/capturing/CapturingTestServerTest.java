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
package com.codemucker.testserver.capturing;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codemucker.match.AList;
import org.codemucker.match.Expect;
import org.junit.After;
import org.junit.Test;

import com.codemucker.testserver.TestServlet;


public class CapturingTestServerTest {

	CapturingTestServer server = new CapturingTestServer();

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	/**
	 * Ensure that the simple startup case works
	 *
	 * @throws Exception
	 */
	@Test
	public void ensure_simple_startup_throws_no_errors() throws Exception {
		server.start();
	}

	@Test
	public void test_captures_all_request_info() throws Exception {
		server.addServlet("/my/path/*", new TestServlet() {
			@Override
			protected void doGet(final HttpServletRequest req,
					final HttpServletResponse resp) throws ServletException,
					IOException {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write("my_servlet");
				resp.getWriter().flush();
			}
		});

		server.start();

		final String url = "http://" + server.getHost() + ":" + server.getHttpPort() + "/my/path/extra/path/info?foo=bar&alice=bob";
		// check we can hit the servlet, that it's run only once, and that we
		// get the params passed to it
		final HttpClient client = new DefaultHttpClient();

		final HttpGet get = new HttpGet(url);
		get.setHeader("Content-Type", "text/html; charset=utf-8");
		final HttpResponse resp = client.execute(get);
		assertEquals(HttpServletResponse.SC_OK, resp.getStatusLine().getStatusCode());
		// check body
		assertEquals("my_servlet",IOUtils.toString(resp.getEntity().getContent()));

		// check we got the captures ok.
		final CapturedRequest expect = new CapturedRequest();
		expect.scheme = "http";
		expect.host = server.getHost();
		expect.port = server.getHttpPort();
		expect.contextPath = "";
		expect.pathInfo = "/extra/path/info";
		expect.servletPath="/my/path";
		expect.method="GET";
		expect.characterEncoding = "UTF-8";
		expect.setParamValues("foo", "bar");
		expect.setParamValues("alice", "bob");

		Expect
			.that(server.getAllRequests())
			.is(AList.withOnly(ACapturedRequest.equalTo(expect)));
		Expect
			.that(server.getRequestsByServletPath("/my/path/*"))
			.is(AList.withOnly(ACapturedRequest.equalTo(expect)));
		
	}

	/**
	 * Ensure that our servlets are wrapped and that requests going in are correctly captured, and
	 * that we can retrieve them afterwards, and that asserts are good
	 *
	 * @throws Exception
	 */
	@Test
	public void test_request_captures_and_retrieval() throws Exception {
		server.addServlet("/my/first/path", new TestServlet() {
			@Override
			protected void doGet(final HttpServletRequest req,
					final HttpServletResponse resp) throws ServletException,
					IOException {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write("first_servlet");
				resp.getWriter().flush();
			}
		});
		server.addServlet("/my/second/path", new TestServlet() {
			@Override
			protected void doGet(final HttpServletRequest req,
					final HttpServletResponse resp) throws ServletException,
					IOException {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write("second_servlet");
				resp.getWriter().flush();
			}
		});
		server.start();

		final String url1 = "http://" + server.getHost() + ":" + server.getHttpPort() + "/my/first/path";
		final String url2 = "http://" + server.getHost() + ":" + server.getHttpPort() + "/my/second/path";

		//check we can hit the servlet, that it's run only once, and that we
		// get the params passed to it

		//make the requests
		final HttpClient client = new DefaultHttpClient();
		// first path
		{
			final HttpGet get = new HttpGet(url1);
			final HttpResponse resp = client.execute(get);

			assertEquals(HttpServletResponse.SC_OK, resp.getStatusLine().getStatusCode());
			// check body
			assertEquals("first_servlet",IOUtils.toString(resp.getEntity().getContent()));
		}
		// second path
		{
			final HttpGet get = new HttpGet(url2);
			final HttpResponse resp = client.execute(get);

			assertEquals(HttpServletResponse.SC_OK, resp.getStatusLine().getStatusCode());
			// check body
			assertEquals("second_servlet",IOUtils.toString(resp.getEntity().getContent()));
		}

		// check we got the captures ok.
		final CapturedRequest req1 = new CapturedRequest();
		req1.scheme = "http";
		req1.host = server.getHost();
		req1.port = server.getHttpPort();
		req1.contextPath = "";
		req1.pathInfo = null;
		req1.servletPath="/my/first/path";
		req1.method="GET";
		req1.characterEncoding = null;

		final CapturedRequest req2 = new CapturedRequest();
		req2.scheme = "http";
		req2.host = server.getHost();
		req2.port = server.getHttpPort();
		req2.contextPath = "";
		req2.pathInfo = null;
		req2.servletPath="/my/second/path";
		req2.method="GET";
		req2.characterEncoding = null;

		//check the server retrieval methods work
		
		Expect
			.that(server.getAllRequests())
			.is(AList.inAnyOrder().withOnly(ACapturedRequest.equalTo(req1)).and(ACapturedRequest.equalTo(req2)));
		Expect
			.that(server.getRequestsByServletPath("/my/first/path"))
			.is(AList.withOnly(ACapturedRequest.equalTo(req1)));
		Expect
			.that(server.getRequestsByServletPath("/my/second/path"))
			.is(AList.withOnly(ACapturedRequest.equalTo(req2)));
	
	}

	

}
