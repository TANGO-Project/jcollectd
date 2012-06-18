package org.collectd.server.http;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.collectd.agent.protocol.Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Httpd {

    private final static String _PREFIX = Network.KEY_PREFIX + "http.";

    private static final boolean ENABLED;

    private final static Integer backlog;
    private final static Integer queue;
    private final static Integer poolsize;
    private final static Integer poolsizemax;
    private final static Integer poolkeepalive;

    private static final InetSocketAddress INETADDR;

    static {
        ENABLED = Boolean.getBoolean(_PREFIX);
        String host = System.getProperty(_PREFIX + "host", null);
        int port = Integer.getInteger(_PREFIX + "port", Network.DEFAULT_PORT);
        INETADDR = host == null ? new InetSocketAddress(port) : new InetSocketAddress(host, port);

        backlog = Integer.getInteger("httpd.backlog", 0);
        queue = Integer.getInteger("httpd.poolq", 500);
        poolsize = Integer.getInteger("httpd.poolsize", 100);
        poolsizemax = Integer.getInteger("httpd.poolsizemax", 1000);
        poolkeepalive = Integer.getInteger("httpd.poolkeepalive", 5);
    }

    private HttpServer httpd;

    public Httpd() throws IOException {

        ThreadPoolExecutor threadPool =
                new ThreadPoolExecutor(poolsize, poolsizemax, poolkeepalive, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(queue));

        httpd = HttpServer.create(INETADDR, backlog);
        httpd.setExecutor(threadPool);
    }

    public final void start() {
        registerShutdownHook(httpd);
        httpd.start();
    }

    public final void addContext(String url, HttpHandler handler) {
        httpd.createContext(url, handler);
    }

    private static void registerShutdownHook(final HttpServer httpd) {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                httpd.stop(poolkeepalive);
            }
        });
    }
}
