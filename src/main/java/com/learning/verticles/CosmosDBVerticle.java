package com.learning.verticles;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.learning.domain.CreditScore;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import reactor.core.publisher.Mono;

import java.util.function.Function;


public class CosmosDBVerticle extends AbstractVerticle {
    private CosmosAsyncClient cosmosClient;
    private CosmosAsyncDatabase cosmosDatabase;
    private CosmosAsyncContainer cosmosContainer;
    private static final String ADDRESS = "credit.score.db";
    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        try {
            ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
                    .setType("file")
                    .setConfig(new JsonObject().put("path", "config/app-config.json"));
            ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
                    .addStore(configStoreOptions);
            ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
            configRetriever.getConfig().onSuccess(config -> {
                String cosmosEndpoint = config.getString("COSMOS_END_POINT");
                String cosmosKey = config.getString("COSMOS_KEY");
                String cosmosDatabaseName = config.getString("COSMOS_DB_NAME");
                String cosmosContainerName = config.getString("COSMOS_DB_CONTAINER_NAME");
                        cosmosClient = new CosmosClientBuilder()
                                .endpoint(cosmosEndpoint)
                                .key(cosmosKey)
                                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                                .contentResponseOnWriteEnabled(true)
                                .buildAsyncClient();
                        cosmosDatabase = cosmosClient.getDatabase(cosmosDatabaseName);
                        cosmosContainer = cosmosDatabase.getContainer(cosmosContainerName);
                        System.out.println("Connected to CosmosDB successfully");
                        this.handle();
                        startPromise.complete();
                    })
                    .onFailure(startPromise::fail);
        } catch (Exception e) {
            System.err.println("Failed to connect to cosmos db due to "+e.getMessage());
            startPromise.fail(e);
        }
    }

    private void handle() {
        CircuitBreaker breaker = CircuitBreaker.create("credit-score-circuit-breaker", vertx,
                new CircuitBreakerOptions().setMaxFailures(5)
                        .setTimeout(2000)
                        .setFallbackOnFailure(true)
        );
        vertx.eventBus().<JsonObject>consumer(ADDRESS, msg -> {
            String customerId = msg.body().getString("customerId");
            Handler<Promise<JsonObject>> operation = promise -> {
                System.out.println("Retrieving credit score from CosmosDB for: "+ customerId);
                String sqlQuery = "SELECT * FROM c WHERE c.customerId = '" + customerId + "'";
                CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
                try {
                    Mono<CreditScore> customers = cosmosContainer.queryItems(
                            sqlQuery,
                            options,
                            CreditScore.class).single();
                    JsonObject jsonObject = new JsonObject();
                    CreditScore creditScore = customers.block();
                    System.out.println("creditScore: " + creditScore);
                    if (creditScore != null) {
                        jsonObject.put("customerId", customerId);
                        jsonObject.put("creditScore", creditScore.getScore());
                        jsonObject.put("source", "primary source");
                        System.out.println("Retrieved credit score from CosmosDB for: " + customerId);
                        promise.complete(jsonObject);
                    } else {
                        promise.fail(new Throwable("unable to retrieve credit score from CosmosDB"));
                    }
                } catch (Exception e) {
                    System.out.println("Error while retrieving credit score from CosmosDB for: " + customerId);
                    throw e;
                }
            };

            Function<Throwable, JsonObject> fallback = ex -> {
                System.out.println("Executing fallback function");
                JsonObject jsonObject = new JsonObject();
                jsonObject.put("customerId", msg.body().getString("customerId"));
                jsonObject.put("creditScore", 760);
                jsonObject.put("source", "secondary source");
                return jsonObject;
            };

            breaker.executeWithFallback(operation, fallback).onComplete(ar -> {
                if (ar.succeeded()) {
                    msg.reply(ar.result());
                } else {
                    msg.reply(ar.cause().getMessage());
                }
            });
        });
    }
}
