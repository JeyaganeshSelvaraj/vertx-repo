package com.ericsson.tmus.simulators.gflex.message.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

public class AdminVerticle extends AbstractVerticle {
  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.get("/").handler(this::handle);
    router.get("/:name").handler(this::handle);
    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }
  private void handle(RoutingContext rc){
    String name = rc.pathParam("name");
    System.out.println("PathParams "+rc.pathParams());
    if(Objects.isNull(name)){
      name= "vert.x";
    }
    JsonObject json = new JsonObject().put("message","Hello "+name);
    rc.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(json.encode());
  }
}
