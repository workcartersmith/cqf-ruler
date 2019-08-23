package org.opencds.cqf.cdshooks.request;

import com.google.gson.JsonObject;

public class OrderSignRequest extends CdsRequest {

    public OrderSignRequest(JsonObject requestJson) {
        super(requestJson);
    }

    @Override
    public void setContext(JsonObject context) {
        this.context = new OrderSignContext(context);
    }
}
