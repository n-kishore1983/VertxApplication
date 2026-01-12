package com.learning.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class CreditScoreVerticle extends AbstractVerticle {

    public static final String ADDRESS = "credit.score";
    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(ADDRESS, msg -> {
            System.out.println("processing message: " + msg.body());
            System.out.println("Retrieving credit score for: "+ msg.body().getString("customerId"));
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("customerId", msg.body().getString("customerId"));
            jsonObject.put("creditScore", 760);
            msg.reply(jsonObject);
        });
    }
}
