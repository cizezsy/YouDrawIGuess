package me.cizezsy.yourdrawiguess.ui.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cizezsy.yourdrawiguess.MyApplication;
import me.cizezsy.yourdrawiguess.R;
import me.cizezsy.yourdrawiguess.model.Chat;
import me.cizezsy.yourdrawiguess.model.Phrase;
import me.cizezsy.yourdrawiguess.model.message.SendToServerMessage;
import me.cizezsy.yourdrawiguess.model.User;
import me.cizezsy.yourdrawiguess.net.MyWebSocketClient;
import me.cizezsy.yourdrawiguess.net.YdigRetrofit;
import me.cizezsy.yourdrawiguess.ui.fragment.ChatFragment;
import me.cizezsy.yourdrawiguess.ui.fragment.GameSettingFragment;
import me.cizezsy.yourdrawiguess.ui.fragment.PhraseSelectFragment;
import me.cizezsy.yourdrawiguess.ui.widget.CleanEditText;
import me.cizezsy.yourdrawiguess.ui.widget.PaintView;
import me.cizezsy.yourdrawiguess.util.JsonUtils;
import okhttp3.Cookie;

public class GameActivity extends AppCompatActivity {

    private static final String SOCKET_SERVER_URL = "ws://115.159.49.186:8080/youdrawiguess/draw";

    private MyWebSocketClient client;

    @BindView(R.id.pv_main)
    PaintView mPaintView;
    @BindView(R.id.pb_game)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_player_num)
    TextView mPlayerTv;
    @BindView(R.id.tv_player_mes)
    TextView mMessageTv;
    @BindView(R.id.tv_game_info)
    TextView mGameInfoTv;
    @BindView(R.id.et_chat_message)
    CleanEditText mChatEt;
    @BindView(R.id.btn_send_chat_mes)
    Button mMessageBtn;
    @BindView(R.id.fragment_container_mes)
    FrameLayout mChatFragmentContainer;
    @BindView(R.id.fragment_container_setting)
    FrameLayout mGameSettingFragmentContainer;

    Fragment mChatFragment;
    Fragment mGameSettingFragment;

    private boolean isChatFragmentOpen = false;
    private boolean isGameSettingFragmentOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        long roomId = getIntent().getIntExtra("roomId", 0);

        List<Cookie> cookieList = YdigRetrofit.cookieStore;

        Map<String, String> cookie = new HashMap<>();
        cookie.put("Cookie", cookieList.get(0).toString());
        client = new MyWebSocketClient(URI.create(SOCKET_SERVER_URL), cookie, this);

        if (roomId != 0L)
            mPaintView.setToMe(false);
        mPaintView.setClient(client);
        mPaintView.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        client.connect();

        FragmentManager fm = getSupportFragmentManager();
        mChatFragment = fm.findFragmentById(R.id.fragment_container_mes);
        if (mChatFragment == null) {
            mChatFragment = new ChatFragment();
            fm.beginTransaction().add(R.id.fragment_container_mes, mChatFragment).commit();
        }

        mGameSettingFragment = fm.findFragmentById(R.id.fragment_container_setting);
        if (mGameSettingFragment == null) {
            mGameSettingFragment = new GameSettingFragment();
            fm.beginTransaction().add(R.id.fragment_container_setting, mGameSettingFragment).commit();
        }


        mMessageBtn.setOnClickListener(v -> {
            String chatMes = mChatEt.getText().toString();
            if (TextUtils.isEmpty(chatMes))
                return;
            Chat chat = new Chat();
            User user = MyApplication.getUser();
            chat.setUsername(user == null ? "未知" : user.getUsername());
            chat.setContent(chatMes);
            SendToServerMessage message = new SendToServerMessage<>(SendToServerMessage.Type.MESSAGE, chat);
            mChatEt.setText("");
            new Thread(() -> client.send(JsonUtils.toJson(message))).start();
        });

        mMessageTv.setOnClickListener(v -> {
            if (mChatFragment == null || mChatFragmentContainer == null)
                return;
            ObjectAnimator mover;
            if (isChatFragmentOpen) {
                mover = ObjectAnimator.ofFloat(mChatFragmentContainer, "translationX", 0.0f, -mChatFragmentContainer.getWidth());
            } else {
                mover = ObjectAnimator.ofFloat(mChatFragmentContainer, "translationX", 0.0f, mChatFragmentContainer.getWidth());

            }
            mover.setDuration(200);
            mover.start();
            isChatFragmentOpen = !isChatFragmentOpen;
//            float animationDirection;
//            if (isChatFragmentOpen) {
//                animationDirection = 1f;
//            } else {
//                animationDirection = -1f;
//            }
//
//            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
//                    Animation.RELATIVE_TO_SELF, animationDirection,
//                    Animation.RELATIVE_TO_SELF, 0f,
//                    Animation.RELATIVE_TO_SELF, 0f);
//            animation.setDuration(200);
//            animation.setFillAfter(true);
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    mChatFragmentContainer.clearAnimation();
//                    RelativeLayout.LayoutParams params
//                            = ((RelativeLayout.LayoutParams) mChatFragmentContainer.getLayoutParams());
//                    params.rightMargin = params.rightMargin + (isChatFragmentOpen ? -params.width : params.width);
//                    mChatFragmentContainer.setLayoutParams(params);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                }
//            });
//
//            mChatFragmentContainer.clearAnimation();
//            mChatFragmentContainer.setAnimation(animation);
//
//            animation.startNow();
//            isChatFragmentOpen = !isChatFragmentOpen;
        });

        mPlayerTv.setOnClickListener(v -> {
            if (mGameSettingFragment == null || mGameSettingFragmentContainer == null)
                return;
            ObjectAnimator mover;
            if (isGameSettingFragmentOpen) {
                mover = ObjectAnimator.ofFloat(mGameSettingFragmentContainer, "translationX", 0.0f, mGameSettingFragmentContainer.getWidth());
            } else {
                mover = ObjectAnimator.ofFloat(mGameSettingFragmentContainer, "translationX", 0.0f, -mGameSettingFragmentContainer.getWidth());

            }
            mover.setDuration(200);
            mover.start();
            isGameSettingFragmentOpen = !isGameSettingFragmentOpen;
        });
        mGameInfoTv.setText("等待开始");
    }

    public void startSelectPhrase(List<Phrase> phraseList) {
        PhraseSelectFragment.newInstance(phraseList, this).show(getSupportFragmentManager(), "phrase");
    }

    public void selectPhrase(Phrase phrase) {
        SendToServerMessage<Phrase> message = new SendToServerMessage<>(SendToServerMessage.Type.PHRASE, phrase);
        client.send(JsonUtils.toJson(message));
    }


    @Override
    protected void onStop() {
        super.onStop();
        client.close();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> finish())
                .setTitle("是否退出")
                .create().show();
    }

    public void hiddenProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void setPlayerNumber(int peopleNumber) {
        mPlayerTv.setText(String.format(Locale.CHINA, "当前游戏人数%d", peopleNumber));
    }

    public void setPlayerMessage(Chat chat) {
        String content = String.format(Locale.CHINA, "%s：%s", chat.getUsername(), chat.getContent());
        if (content.length() > 18) {
            content = content.substring(0, 18) + "....";
        }
        if (chat.getType() == Chat.Type.SYSTEM) {
            mMessageTv.setTextColor(Color.RED);
        } else {
            mMessageTv.setTextColor(Color.BLACK);
        }
        mMessageTv.setText(content);
    }

    public void setGameInfo(String info) {
        mGameInfoTv.setText(info);
    }

    public List<Chat> getChatList() {
        return client.getChatList();
    }

    public void notifyChatAdd() {
        ((ChatFragment) mChatFragment).notifyChatAdd();
    }

    public void setPaintColor(int color) {
        mPaintView.setColor(color);
    }

    public void setPaintStrokeWidth(float width) {
        mPaintView.setStrokeWidth(width);
    }
}
