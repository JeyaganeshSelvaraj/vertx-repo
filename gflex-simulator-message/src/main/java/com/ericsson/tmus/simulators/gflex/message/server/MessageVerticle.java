package com.ericsson.tmus.simulators.gflex.message.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class MessageVerticle extends AbstractVerticle {
  @Override
  public void start() {
    vertx.eventBus().<String>consumer("hello", message -> {
      double chaos = Math.random();
      if (Boolean.getBoolean("success-mode") || chaos < 0.6) {
        reply(message);
      } else {
        failOrNoReply(message, chaos);
      }
    });

  }

  private void failOrNoReply(Message<String> message, double chaos) {
    if (chaos < 0.9) {
      System.out.println("Returning a failure");
      // Reply with a failure
      message.fail(500,
        "message processing failure");
    }else{
      System.out.println("Not replying");
      // Just do not reply, leading to a timeout on the
      // consumer side.
    }
  }

  private void reply(Message<String> message) {
    JsonObject json = new JsonObject();
    json.put("served-by", this.toString());
    String body = message.body();
    if (body.isEmpty()) {
      body = "World";
    }
    message.reply(json.put("message", "Hello, " + body));
  }
}
