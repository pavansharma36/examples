package org.one.vertx.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(httpServerRequest -> httpServerRequest.response().end("Hello World!"))
                // Start listening
                .listen(8888)
                // Print the port
                .onSuccess(server -> {
                    log.info("HTTP server started on port {}", server.actualPort());
                    startPromise.complete();
                }).onFailure(startPromise::fail);
    }
}
