package com.tobaccocessation.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.MainActivity;
import com.tobaccocessation.R;
import com.tobaccocessation.activity.AskCreateAccountActivity;
import com.tobaccocessation.activity.Landing_3Activity;
import com.tobaccocessation.activity.SuggestionActivity;
import com.tobaccocessation.activity.VideoStatusActivity;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;

import com.tobaccocessation.chat.channels.ChannelManager;
import com.tobaccocessation.chat.channels.LoadChannelListener;
import com.tobaccocessation.chat.listeners.TaskCompletionListener;
import com.tobaccocessation.chat.messages.AlertDialogHandler;
import com.tobaccocessation.model.NotificationModel;
import com.twilio.chat.Channel;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;


import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class MainChatActivity extends AppCompatActivity implements ChatClientListener, View.OnClickListener {
    private Context context;
    private Activity mainActivity;
    private Button logoutButton;
    private Button addChannelButton;
    private TextView usernameTextView;
    private ChatClientManager chatClientManager;
    private ListView channelsListView;
    private ChannelManager channelManager;
    private MainChatFragment chatFragment;
    private DrawerLayout drawer;
    private ProgressDialog progressDialog;
    private MenuItem leaveChannelMenuItem;
    private MenuItem deleteChannelMenuItem;
    SwipeRefreshLayout pullToRefresh;
    SharedPreferences sharedPreferences;
    public  static String channel_name;
    ImageView iv_navi, iv_video, iv_chat;
    SharedPreferences.Editor editor;
    Channel selectedChannel;
    private String TAG;
    NotificationModel notificationModel;
    CircularProgressBar circularProgressBar;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                chatClientManager.shutdown();
                TwilioChatApplication.get().getChatClientManager().setChatClient(null);
                TwilioChatApplication.get().getChatClientManager().setChatClient(null);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        circularProgressBar = (CircularProgressBar)findViewById(R.id.homeloader);

        setSupportActionBar(toolbar);
        TAG = "MainChatActivity";
        initializeIds();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF, 0);
        channel_name = sharedPreferences.getString("channel_name", "");
        LocalBroadcastManager.getInstance(MainChatActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = sharedPreferences.edit();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        chatFragment = new MainChatFragment();
        fragmentTransaction.add(R.id.fragment_container, chatFragment);
        fragmentTransaction.commit();

        context = this;
        mainActivity = this;
        channelManager = ChannelManager.getInstance();
        checkTwilioClient();
    }

    private void initializeIds() {
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_navi = findViewById(R.id.iv_navi);

        iv_chat.setOnClickListener(this);
        iv_navi.setOnClickListener(this);
        iv_video.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = sharedPreferences.getBoolean("isbooleanVideo", false);
        boolean isChat = sharedPreferences.getBoolean("isbooleanChat", false);
        if (isVideo == true) {
            iv_video.setImageResource(R.drawable.video_icon);
        } else if (isChat == true) {
            iv_chat.setImageResource(R.drawable.chat_icon);

        }

    }

    //broadcast receiver for video chat calling
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                notificationModel = (NotificationModel) intent.getSerializableExtra("notificationData");
                if (notificationModel.getMethod().equalsIgnoreCase("video")) {
                    iv_video.setImageResource(R.drawable.video_icon);
                    editor.putBoolean("isbooleanVideo", true);
                    editor.commit();

                } else if (notificationModel.getMethod().equalsIgnoreCase("chat")) {
                    iv_chat.setImageResource(R.drawable.chat_icon);
                    editor.putBoolean("isbooleanChat", true);
                    editor.commit();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_navi:
                Intent i = new Intent(MainChatActivity.this, Landing_3Activity.class);
                startActivity(i);
                finish();
                break;

            case R.id.iv_video:
                i = new Intent(MainChatActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

            case R.id.iv_chat:
                i = new Intent(MainChatActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
        }

    }
    private String getStringResource(int id) {
        Resources resources = getResources();
        return resources.getString(id);
    }

    private void populateChannels() {
        channelManager.setChannelListener(this);
        channelManager.populateChannels(new LoadChannelListener() {
            @Override
            public void onChannelsFinishedLoading(List<Channel> channels) {
                MainChatActivity.this.channelManager
                        .joinOrCreateGeneralChannelWithCompletion(new StatusListener() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopActivityIndicator();
                                        setChannel(0);
                                    }
                                });
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                //showAlertWithMessage(getStringResource(R.string.generic_error));
                            }
                        });
            }
        });

    }


    private void setChannel(final int position) {
        List<Channel> channels = channelManager.getChannels();
        if (channels == null) {
            return;
        }
        final Channel currentChannel = chatFragment.getCurrentChannel();
        for (int i = 0; i < channels.size() - 1; i++) {
            if (channels.get(i).getUniqueName().equalsIgnoreCase(getResources().getString(R.string.default_channel_unique_name))) {
                selectedChannel = channels.get(i);
                break;
            } else {

            }
        }
        if (this.selectedChannel != null) {
            showActivityIndicator("Joining " + this.selectedChannel.getFriendlyName() + " channel");
            if (currentChannel != null && currentChannel.getStatus() == Channel.ChannelStatus.JOINED) {
                this.channelManager.leaveChannelWithHandler(currentChannel, new StatusListener() {
                    @Override
                    public void onSuccess() {
                        joinChannel(MainChatActivity.this.selectedChannel);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        stopActivityIndicator();
                    }
                });
                return;
            }
            joinChannel(this.selectedChannel);
            stopActivityIndicator();
        } else {
            stopActivityIndicator();
            //showAlertWithMessage(getStringResource(R.string.generic_error));
            System.out.println("Selected channel out of range");
        }
    }

    private void joinChannel(final Channel selectedChannel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatFragment.setCurrentChannel(selectedChannel, new StatusListener() {
                    @Override
                    public void onSuccess() {
                        MainChatActivity.this.stopActivityIndicator();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
                setTitle(selectedChannel.getFriendlyName());
                // drawer.closeDrawer(GravityCompat.START);
            }
        });
    }


    private void checkTwilioClient() {
        showActivityIndicator(getStringResource(R.string.loading_channels_message));
        chatClientManager = TwilioChatApplication.get().getChatClientManager();
        if (chatClientManager.getChatClient() == null) {
            initializeClient();
        } else {
            populateChannels();
        }
    }

    private void initializeClient() {
        chatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(Void aVoid) {
                populateChannels();
            }

            @Override
            public void onError(String errorMessage) {
                stopActivityIndicator();
                showAlertWithMessage("Client connection error");
            }
        });
    }

    private void stopActivityIndicator() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                circularProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showActivityIndicator(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                progressDialog = new ProgressDialog(MainChatActivity.this.mainActivity);
//                progressDialog.setMessage(message);
//                progressDialog.show();
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setCancelable(false);
                circularProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAlertWithMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // AlertDialogHandler.displayAlertWithMessage(message, context);
            }
        });
    }


    @Override
    public void onChannelAdded(Channel channel) {
        System.out.println("Channel Added");
        // refreshChannels();
    }

    @Override
    public void onChannelDeleted(final Channel channel) {
        System.out.println("Channel Deleted");
        Channel currentChannel = chatFragment.getCurrentChannel();
        if (channel.getSid().contentEquals(currentChannel.getSid())) {
            chatFragment.setCurrentChannel(null, null);
            setChannel(0);
        }
        //  refreshChannels();
    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {

    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }

    @Override
    public void onChannelJoined(Channel channel) {

    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {

    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {

    }

    @Override
    public void onAddedToChannelNotification(String s) {

    }

    @Override
    public void onInvitedToChannelNotification(String s) {

    }

    @Override
    public void onRemovedFromChannelNotification(String s) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }


}
