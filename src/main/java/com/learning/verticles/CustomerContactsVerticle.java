package com.learning.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class CustomerContactsVerticle extends AbstractVerticle {
    public static final String ADDRESS = "customer.contacts";
    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(ADDRESS, msg -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("street", "123 Main St");
            jsonObject.put("city", "Dallas");
            jsonObject.put("state", "TX");
            jsonObject.put("zip", "1234");
            jsonObject.put("country", "US");
            msg.reply(new JsonObject().put("address", jsonObject));
        });

    }
}
