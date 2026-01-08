package com.learning.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

public class CustomersVerticle extends AbstractVerticle {
    private Router router;
    public CustomersVerticle() {
        this.router = Router.router(vertx);
    }
    @Override
    public void start() throws Exception {
        router
                .route(HttpMethod.GET, "/customers/:customerId")
                .handler(ctx -> {
                    String customerId = ctx.pathParam("customerId");
                    System.out.println("Retrieving Details for customer " + customerId);
                    JsonObject payload = new JsonObject().put("customerId", customerId);
                    Future<Message<JsonObject>> creditScoreFuture = vertx.eventBus().<JsonObject>request("credit.score", payload);
                    Future<Message<JsonObject>> contactFuture = vertx.eventBus().<JsonObject>request("customer.contacts", payload);

                    List<Future<Message<JsonObject>>> futures = new ArrayList<>();
                    futures.add(creditScoreFuture);
                    futures.add(contactFuture);

                    JsonArray results = new JsonArray();
                    JsonObject mergedObject = new JsonObject();
                    futures.forEach(e -> e.onSuccess(reply -> mergedObject.mergeIn(reply.body())));

                    CompositeFuture compositeFuture = Future.all(futures);

                    compositeFuture.onComplete(ar -> {
                        ctx.response()
                                .putHeader("content-type", "text/plain")
                                .end(mergedObject.encodePrettily());
                    });
                });
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}
