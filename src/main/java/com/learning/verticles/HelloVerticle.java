package com.learning.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;


public class HelloVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> future) {
        System.out.println("Deploying HelloVerticle");
    }
}
