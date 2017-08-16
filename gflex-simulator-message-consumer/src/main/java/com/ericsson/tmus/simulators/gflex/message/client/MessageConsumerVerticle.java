package com.ericsson.tmus.simulators.gflex.message.client;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Single;

import java.util.concurrent.TimeUnit;


public class MessageConsumerVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.createHttpServer().requestHandler(req -> {
      jsonMessageHandler().subscribe(x -> req.response().end(x.encodePrettily()),
        t -> {
          t.printStackTrace();
          req.response().setStatusCode(500).end(t.getMessage());
        });
    }).listen(8082);

  }

  private Single<JsonObject> jsonMessageHandler() {
    EventBus bus = vertx.eventBus();
    Single<JsonObject> obs1 = bus.<JsonObject>rxSend("hello", "Luke")
              .subscribeOn(RxHelper.scheduler(vertx)).retry().timeout(3, TimeUnit.SECONDS).map(Message::body);
    Single<JsonObject> obs2 = bus.<JsonObject>rxSend("hello", "Leia")
              .subscribeOn(RxHelper.scheduler(vertx)).retry().timeout(3, TimeUnit.SECONDS).map(Message::body);
    return Single.zip(obs1, obs2, (l1, l2) ->
      new JsonObject().put("Luke", l1.getString("message") + " from " + l1.getString("served-by"))
        .put("Leia", l2.getString("message") + " from " + l2.getString("served-by")));

  }

}



