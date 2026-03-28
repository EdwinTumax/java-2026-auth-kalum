package edu.kalum.auth.core.dtos;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;


public class ApiResponseDTO {
    public static JsonObject success(Object data) {
        return new JsonObject()
                .put("success",true)
                .put("message","Success")
                .put("data", data == null ? null : wrap(data))
                .put("errors",(Object) null);
    }

    public static JsonObject created(Object data) {
        return new JsonObject()
                .put("success",true)
                .put("message","Created successfully")
                .put("data", data == null ? null : wrap(data))
                .put("errors",(Object) null);
    }

    public static JsonObject error(String message, String error) {
        return error(message, List.of(error));
    }

    public static JsonObject error(String message, List<String> errors) {
        return new JsonObject()
                .put("success",false)
                .put("message", message)
                .put("data", (Object) null)
                .put("errors", errors == null ? new JsonArray() : new JsonArray(errors));
    }

    private static Object wrap(Object data) {
        if(data instanceof io.vertx.core.json.JsonObject || data instanceof io.vertx.core.json.JsonArray) {
            return data;
        }
        return io.vertx.core.json.JsonObject.mapFrom(data);
    }
}
