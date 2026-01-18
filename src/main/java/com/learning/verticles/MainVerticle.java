package com.learning.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() {
        Router router = Router.router(vertx);
        vertx.deployVerticle(new CustomersVerticle());
        vertx.deployVerticle(new CreditScoreVerticle());
        vertx.deployVerticle(new CustomerContactsVerticle());
        vertx.deployVerticle("groovy:groovy/EmploymentVerticle.groovy");
        vertx.deployVerticle(new CosmosDBVerticle());
    }
}
