package org.one.vertx.server;

import io.vertx.core.Vertx;
import sun.misc.Signal;

public class App {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ServerVerticle.class.getName());
        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));
    }
}
