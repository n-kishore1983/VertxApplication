package groovy

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject

class EmploymentVerticle extends AbstractVerticle{

    public static final String ADDRESS = "employment.info";
    @Override
    void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(ADDRESS, msg-> {
            System.out.println("Retrieving Employment Info for: "+ msg.body().getString("customerId"));
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("employer", "ABC Inc");
            jsonObject.put("salary", "100000");
            jsonObject.put("currency", "USD");
            jsonObject.put("startDate", "2025-01-01");

            msg.reply(new JsonObject().put("employmentDetails", jsonObject));
        });
    }
}
