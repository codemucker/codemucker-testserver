package com.bertvanbrakel.testserver.capturing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bertvanbrakel.testserver.Server;
import com.bertvanbrakel.testserver.TestServer;
import com.bertvanbrakel.testserver.TestServlet;
/**
 * A MockServer which captures all requests for later analysis
 * of correct client behaviour
 *
 * @author Bert van Brakel
 *
 */
public class CapturingTestServer implements Server {

	private final Object LOCK  = new Object();

	private final TestServer server = new TestServer();

	//needs to be thread safe for concurrent read/write
	private final Collection<CapturedRequest> allRequests = new Vector<CapturedRequest>();
	//needs to be thread safe for concurrent read/write
	private final Map<String, List<CapturedRequest>> allRequestsByServletPath = new ConcurrentHashMap<String, List<CapturedRequest>>();

	@Override
    public void start() throws Exception {
		server.start();
	}

	@Override
    public void stop() throws Exception {
		server.stop();
	}

	public void addServlet(final String servletPath, final HttpServlet servlet) {
		final TestServlet wrapper = new TestServlet() {
            private static final long serialVersionUID = 1L;
            @Override
			public void service(final HttpServletRequest req, final HttpServletResponse res)
					throws ServletException, IOException {
				//capture the request for later analysis
				captureRequest(servletPath, req);
				//perform the request
				servlet.service(req, res);
			}
		};
		server.addServlet(servletPath, wrapper);
	}

	private void captureRequest(final String key, final HttpServletRequest req) {
		final CapturedRequest capture = new CapturedRequest(req);
		synchronized(LOCK){
			allRequests.add(capture);
			// ensure we have a list to add to
			if (!allRequestsByServletPath.containsKey(key)) {
				allRequestsByServletPath.put(key, new ArrayList<CapturedRequest>());
			}
			allRequestsByServletPath.get(key).add(capture);
		}
	}

	/**
	 * Clear the captured requests
	 */
	public void resetCaptures() {
		allRequestsByServletPath.clear();
		allRequests.clear();
	}

	@Override
    public String getHost() {
		return server.getHost();
	}

	@Override
    public int getHttpPort() {
		return server.getHttpPort();
	}

	public String getBaseHttpUrl(){
		return server.getBaseHttpUrl();
	}

	/**
	 * Returns a list of all the requests captured by servlet path. Modifiable, but changes are not reflected
	 *
	 * @return
	 */
	public List<CapturedRequest> getRequestsByServletPath(
			final String servletPath) {
		final List<CapturedRequest> requests = allRequestsByServletPath
				.get(servletPath);
		return requests == null ? new ArrayList<CapturedRequest>() : requests;
	}

	/**
	 * Returns a list of all the requests captured. Modifiable, but changes are not reflected
	 *
	 * @return
	 */
	public List<CapturedRequest> getAllRequests() {
		return new ArrayList<CapturedRequest>(allRequests);
	}

    public int getTotalNumRequests() {
        return allRequests.size();
    }

    public int getNumRequestsForPath(final String servletPath) {
        final Collection<CapturedRequest> col = allRequestsByServletPath.get(servletPath);
        return col==null?0:col.size();
    }

}
