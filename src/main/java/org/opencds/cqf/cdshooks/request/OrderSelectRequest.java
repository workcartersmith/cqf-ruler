package org.opencds.cqf.cdshooks.request;

import com.google.gson.JsonObject;

public class OrderSelectRequest extends CdsRequest {

    public OrderSelectRequest(JsonObject requestJson) {
        super(requestJson);
    }

    @Override
    public void setContext(JsonObject context) {
        this.context = new OrderSelectContext(context);
    }
}
