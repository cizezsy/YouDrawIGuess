package me.cizezsy.yourdrawiguess.net;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.cizezsy.yourdrawiguess.model.Step;
import me.cizezsy.yourdrawiguess.model.GameMessage;
import me.cizezsy.yourdrawiguess.ui.activity.GameActivity;
import me.cizezsy.yourdrawiguess.ui.widget.PaintView;
import me.cizezsy.yourdrawiguess.util.JsonUtils;

public class MyWebSocketClient extends WebSocketClient {

    private final static int NONE = 0;
    private final static int PLAYER_CHANGE = 1;
    private final static int DRAW = 3;
    private final static int TO_ME = 4;
    private final static int RESUME = 5;

    private PaintView mPaintView;
    private Activity mActivity;

    public MyWebSocketClient(URI serverURI, Map<String, String> header, Activity activity) {
        super(serverURI, new Draft_17(), header, 12000);
        this.mActivity = activity;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Log.d("webSocket", "open");
        mActivity.runOnUiThread(() -> {
            ((GameActivity) mActivity).hiddenProgressBar();
            mPaintView.setEnabled(true);
        });
    }

    @Override
    public void onMessage(String message) {
        GameMessage gameMessage = JsonUtils.fromJson(message, GameMessage.class);
        JsonElement data = gameMessage.getData();
        Log.d("webSocket", "message" + data);
        try {
            switch (gameMessage.getType()) {
                case NONE:
                    break;
                case PLAYER_CHANGE:
                    Integer playerNum = JsonUtils.fromJson(data, Integer.class);
                    mActivity.runOnUiThread(() -> {
                        ((GameActivity) mActivity).setPlayerNumber(playerNum);
                    });
                    Log.d("webSocket", "Player number: " + playerNum);
                    break;
                case DRAW:
                    Step step = JsonUtils.fromJson(data, Step.class);
                    mActivity.runOnUiThread(() -> mPaintView.refreshPath(step));
                    break;
                case TO_ME:
                    mPaintView.setToMe(true);
                    break;
                case RESUME:
                    Type stepListType = new TypeToken<ArrayList<Step>>() {
                    }.getType();
                    List<Step> stepList = JsonUtils.fromJson(data, stepListType);
                    mActivity.runOnUiThread(() -> mPaintView.refreshPath(stepList));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("webSocket", "close");
    }

    @Override
    public void onError(Exception ex) {
        Log.d("webSocket", "error: " + ex.getMessage());
    }

    public void setPaintView(PaintView paintView) {
        mPaintView = paintView;
    }
}
