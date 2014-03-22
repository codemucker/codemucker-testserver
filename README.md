I am a thin wrapper around jetty to make it easy to spin up a webserver for testing.

This can be useful when stubbing out the server when testing web clients for example


Example

TestServer server = new TestServer();
server.setHttpPort(8080);//if not set picks a random one
server.addServlet("/hello", new TestServlet() {
            @Override
            protected void service(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException,IOException {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    final PrintWriter w = resp.getWriter();
                        w.println("Hello everyone");
                        w.flush();
}});

try {
    start();
    //hanlde JVM shutdonw gracefully
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
            server.stop();
        }
    }));
    //let the server run for a bit
    while (true) {
        Thread.yield();
        Thread.sleep(1000 * 30);//30 secs
    }
} finally {
    server.stop();
}


		