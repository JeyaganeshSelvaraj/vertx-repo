package com.ericsson.tmus.simulators.gflex.message.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.Objects;

public class ClientVerticle extends AbstractVerticle {

  private WebClient client;

  @Override
  public void start() {
    client = WebClient.create(vertx);
    Router router = Router.router(vertx);
    router.get("/").handler(this::invokeGflexServer);
    router.get("/:name").handler(this::invokeGflexServer);
    vertx.createHttpServer().requestHandler(router::accept).listen(8081);
  }

  private void invokeGflexServer(RoutingContext rc) {
    String name = rc.pathParam("name");
    name = Objects.isNull(name) ? "World" : name;
    HttpRequest<JsonObject> req = client.get(8081, "localhost", "/"+name)
                                        .as(BodyCodec.jsonObject());
    req.send(ar->{
      if(ar.succeeded()){
        rc.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json").end(ar.result().body().encode());
      }else{
        rc.fail(ar.cause());
      }
    });
  }
}
