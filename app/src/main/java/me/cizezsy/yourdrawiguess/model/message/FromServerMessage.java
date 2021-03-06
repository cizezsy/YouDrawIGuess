package me.cizezsy.yourdrawiguess.model.message;


import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;


public class FromServerMessage {
    @SerializedName("type")
    private int type;
    @SerializedName("data")
    private JsonElement data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }
}
