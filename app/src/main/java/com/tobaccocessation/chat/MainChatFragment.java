package com.tobaccocessation.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tobaccocessation.R;
import com.tobaccocessation.chat.messages.JoinedStatusMessage;
import com.tobaccocessation.chat.messages.MessageAdapter;
import com.tobaccocessation.chat.messages.StatusMessage;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;
import com.twilio.chat.StatusListener;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static com.tobaccocessation.chat.accesstoken.TwilioChatApplication.TAG;

public class MainChatFragment extends Fragment implements ChannelListener {
    Context context;
    public static Activity mainActivity;
    Button sendButton;
  //  ListView messagesListView;
  private final int RECIEVER_TYPE = 0;
    private final int SENDER_TYPE = 1;
    RecyclerView messagesListView;
    public static EditText messageTextEdit;

    public static MessageAdapter messageAdapter;
    public static Channel currentChannel;
    public static Messages messagesObject;
    public static long index;
    private static SwipeRefreshLayout pullToRefresh;
    public static Boolean istymloadedmessage = false;
    private static long topIndex = -1;
CircularProgressBar circularProgressBar;
    public MainChatFragment() {
    }

    public static MainChatFragment newInstance() {
        MainChatFragment fragment = new MainChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        mainActivity = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_chat, container, false);
        sendButton = (Button) view.findViewById(R.id.buttonSend);
        messagesListView = (RecyclerView) view.findViewById(R.id.listViewMessages);
        messageTextEdit = (EditText) view.findViewById(R.id.editTextMessage);
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pullToRefresh);
        messagesListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageAdapter = new MessageAdapter(mainActivity, messagesListView);
        messagesListView.setAdapter(messageAdapter);
        setUpListeners();
        setMessageInputEnabled(false);


        messageTextEdit.addTextChangedListener(new TextWatcher() {


                                                   @Override
                                                   public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                                       if (cs.length() == 0)
                                                           sendButton.setVisibility(View.GONE);
                                                       else
                                                           sendButton.setVisibility(View.VISIBLE);
                                                   }

                                                   @Override

                                                   public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                                                 int arg3) {
                                                       // TODO Auto-generated method stub

                                                   }

                                                   @Override
                                                   public void afterTextChanged(Editable arg0) {
                                                       // TODO Auto-generated method stub
                                                   }
                                               }

        );

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadPreviousMessages();

            }
        });

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }

    public void setCurrentChannel(Channel currentChannel, final StatusListener handler) {
        if (currentChannel == null) {
            this.currentChannel = null;
            return;
        } else {
            this.currentChannel = currentChannel;

    /*  if (!currentChannel.equals(this.currentChannel)) {*/
            setMessageInputEnabled(false);
            this.currentChannel = currentChannel;
            this.currentChannel.addListener(this);
            if (this.currentChannel.getStatus() == Channel.ChannelStatus.JOINED) {
                loadMessages(handler);
            } else {
                this.currentChannel.join(new StatusListener() {
                    @Override
                    public void onSuccess() {
                        loadMessages(handler);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
            }
        }
        // }
    }

    private void loadMessages(final StatusListener handler) {

        istymloadedmessage = true;
        this.messagesObject = this.currentChannel.getMessages();
        //Log.e(TAG, "messages count==="+currentChannel.getMessages().getLastConsumedMessageIndex());
        if (messagesObject != null) {
            messagesObject.getLastMessages(20, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messageList) {
                    try {
                    messageAdapter.setMessages(messageList);
                        setMessageInputEnabled(true);
                        messageTextEdit.requestFocus();
                        handler.onSuccess();
                        if(!messageList.isEmpty()) {
                            index = messageList.get(messageList.size() - 1).getMessageIndex();
                            topIndex = messageList.get(0).getMessageIndex();
                            //groupDataIntoHashMap(messageList);
                        }
                    }
                    catch(Exception e) {
                        Log.e(TAG, "loadMessages:  "+e.getMessage());
                    }
                }
            });
        }
    }

    public void loadPreviousMessages() {
        messagesObject = currentChannel.getMessages();
        // index = index - 10;

        if (messagesObject != null) {
            if(topIndex != 0) {
                messagesObject.getMessagesBefore(index - 20, 20, new CallbackListener<List<Message>>() {

                    @Override
                    public void onSuccess(List<Message> messageList) {
                        try {
                            messageAdapter.setMessages(messageList);
                            setMessageInputEnabled(true);
                            messageTextEdit.requestFocus();
                            //  handler.onSuccess();
                            pullToRefresh.setRefreshing(false);
                            if(!messageList.isEmpty()) {
                                index = messageList.get(messageList.size() - 1).getMessageIndex();
                                topIndex = messageList.get(0).getMessageIndex();
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, "loadPreviousMessages:  "+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                        pullToRefresh.setRefreshing(false);
                    }
                });
            }
            else {
                pullToRefresh.setRefreshing(false);
            }
        }
    }

    private void setUpListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        messageTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
    }


    private void sendMessage() {
        String messageText = getTextInput();
        if (messageText.length() == 0) {
            return;
        }
        Message.Options messageOptions = Message.options().withBody(messageText);
        this.messagesObject.sendMessage(messageOptions, null);
        clearTextInput();
    }

    public void setMessageInputEnabled(final boolean enabled) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainChatFragment.this.sendButton.setEnabled(enabled);
                MainChatFragment.this.messageTextEdit.setEnabled(enabled);
            }
        });
    }

    private String getTextInput() {
        return messageTextEdit.getText().toString();
    }

    private void clearTextInput() {
        messageTextEdit.setText("");
    }





    @Override
    public void onMessageAdded(Message message) {
        messageAdapter.addMessage(message);
    }

    @Override
    public void onMemberAdded(Member member) {
        StatusMessage statusMessage = new JoinedStatusMessage(member.getIdentity());
        //this.messageAdapter.addStatusMessage(statusMessage);
    }

    @Override
    public void onMemberDeleted(Member member) {
        //StatusMessage statusMessage = new LeftStatusMessage(member.getIdentity());
        //this.messageAdapter.addStatusMessage(statusMessage);
    }

    @Override
    public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
    }

    @Override
    public void onMessageDeleted(Message message) {
    }

    @Override
    public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {
    }

    @Override
    public void onTypingStarted(Channel channel, Member member) {
    }

    @Override
    public void onTypingEnded(Channel channel, Member member) {
    }

    @Override
    public void onSynchronizationChanged(Channel channel) {
    }


}
