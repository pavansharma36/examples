package org.one.vertx.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerVerticle extends AbstractVerticle {

    static final AtomicReference<HttpServer> SERVER_REF = new AtomicReference<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      Router router = Router.router(vertx);
      router.get("/").handler(ctx -> ctx.end("Hello World"));
      router.get("/heavy").blockingHandler(ctx -> {
        try {
          Thread.sleep(TimeUnit.SECONDS.toMillis(15L));
        } catch (InterruptedException e) {
          // NOTHING
        }
        ctx.end("Processed");
      });
        Future<HttpServer> fServer = vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(router)
                // Start listening
                .listen(8888)
                // Print the port
                .onSuccess(server -> {
                    log.info("HTTP server started on port {}", server.actualPort());
                    startPromise.complete();
                }).onFailure(startPromise::fail);
        fServer.onSuccess(SERVER_REF::set);
    }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
      HttpServer server = SERVER_REF.get();
        if (server != null) {
          try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(20L));
          } catch (InterruptedException e) {
          }
          server.close();
        }
        SERVER_REF.set(null);
        stopPromise.complete();
  }
}
