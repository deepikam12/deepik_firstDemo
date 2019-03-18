package com.tobaccocessation.chat.messages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tobaccocessation.R;

import com.twilio.chat.Message;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.WeakHashMap;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MessageAdapter";

    Context context;
    private int lastVisibleItem, totalItemCount;
    private List<ChatMessage> messages;
    private TreeSet<Integer> statusMessageSet = new TreeSet<>();
    ListView listView;
    RecyclerView recyclerView;
    private LayoutInflater mLayoutInflater;
    private final int RECIEVER_TYPE = 0;
    private final int SENDER_TYPE = 1;
    private static final int TYPE_ITEM_DATE_CONTAINER = 2;
    private HashMap<String, List<ChatMessage>> mData = new HashMap<>();
    private ArrayList<String> mKeys;
    ArrayList<HashMap<String, List<ChatMessage>>> hashMapArrayList;
    String date="";
    Boolean flag = false;

    public MessageAdapter(Activity activity, RecyclerView recyclerView) {
        //  layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<>();
        this.context = activity;
        this.recyclerView = recyclerView;
        this.mData = mData;
        this.hashMapArrayList = hashMapArrayList;

        mKeys = new ArrayList<String>(mData.keySet());
        mLayoutInflater = LayoutInflater.from(activity);
    }

    public String getKey(int position) {
        return (String) mKeys.get(position);
    }
        /*listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.e(TAG, "On Scroll");

                int currentItemPosition = totalItemCount - firstVisibleItem;
                if (firstVisibleItem== 0 && MainChatFragment.istymloadedmessage!=false)
                    MainChatFragment.loadPreviousMessages();
                else {

                }
                // }
            }
        });*/

    @Override
    public int getItemViewType(int position) {

        if (messages.get(position).getAuthor().equalsIgnoreCase("Sushmita")) {
            return SENDER_TYPE; // sender row;
        } else {
            return RECIEVER_TYPE; // receiver row;
        }
//       }

    }

    public void setMessages(List<Message> messages) {
        this.messages = convertTwilioMessages(messages);
        //this.statusMessageSet.clear();
        notifyDataSetChanged();

    }

    public void addMessage(Message message) {
        messages.add(new UserMessage(message));
        notifyDataSetChanged();
    }
//
//    public MessageAdapter(List<ListObject> listObjects) {
//        this.listObjects = listObjects;
//    }
//
//    public void addStatusMessage(StatusMessage message) {
//        messages.add(message);
//        //statusMessageSet.add(messages.size() - 1);
//        notifyDataSetChanged();
//    }
//
//    public void setDataChange(List<ListObject> asList) {
//        this.listObjects = asList;
//        //now, tell the adapter about the update
//        notifyDataSetChanged();
//    }

    public void removeMessage(Message message) {
        messages.remove(messages.indexOf(message));
        notifyDataSetChanged();
    }

    private List<ChatMessage> convertTwilioMessages(List<Message> messages) {
        //List<ChatMessage> chatMessages = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            this.messages.add(0, new UserMessage(messages.get(i)));
        }

        return this.messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_list_item, parent, false);
            return new SenderViewHolder(view);
        } else if ((viewType == RECIEVER_TYPE)) {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_list_items, parent, false);
            return new ReceiverViewHolder(view);
        }
//        else if ((viewType == TYPE_ITEM_DATE_CONTAINER)) {
//            View view = LayoutInflater.from(context).inflate(R.layout.message, parent, false);
//            return new ReceiverViewHolder(view);
//        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        try {
            final ChatMessage message = messages.get(position);
//            String key = getKey(position);
//            HashMap<String, List<ChatMessage>> value = hashMapArrayList.get(position);
            if (holder instanceof SenderViewHolder) {

                ((SenderViewHolder) holder).textViewMessage.setText(message.getMessageBody());
                ((SenderViewHolder) holder).textViewAuthor.setText(message.getAuthor());
                ((SenderViewHolder) holder).textViewDate.setText(DateFormatter.getFormattedDateFromISOString(message.getDateCreated()));

//                ((SenderViewHolder) holder).tv_message_sender.setTextColor(context.getResources().getColor(R.color.white));
//                ((SenderViewHolder) holder). tv_message_sender.setBackground(context.getResources().getDrawable(R.drawable.msg_bg));
            } else if (holder instanceof ReceiverViewHolder) {

                if (date.equalsIgnoreCase((messages.get(position).getDateCreated()).substring(0, 10))){
                    ((ReceiverViewHolder) holder).tv_date.setVisibility(View.GONE);
                }
                else {
                    date = (messages.get(position).getDateCreated().substring(0, 10));

                    Date today = Calendar.getInstance().getTime();


                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateObj = curFormater.parse(date);
                    SimpleDateFormat postFormater = new SimpleDateFormat("MMM dd, yyyy");

                    String newDateStr = postFormater.format(dateObj);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String todayDate = df.format(today);
                    if(date.equalsIgnoreCase(todayDate))
                    {
                        ((ReceiverViewHolder) holder).tv_date.setVisibility(View.VISIBLE);
                        ((ReceiverViewHolder) holder).tv_date.setText("Today");
                    }

                    else {
                        ((ReceiverViewHolder) holder).tv_date.setVisibility(View.VISIBLE);
                        ((ReceiverViewHolder) holder).tv_date.setText(newDateStr);
                    }
                }
//                for (int i = 0; i < messages.size(); i++) {
//                    if (flag == false) {
//                        date = ((messages.get(0).getDateCreated()).substring(0, 10));
//
//                    }
//                    if (date.equalsIgnoreCase((messages.get(i).getDateCreated()).substring(0, 10))) {
//                        flag = true;
//                        if (i == 0) {
//                            ((ReceiverViewHolder) holder).tv_date.setVisibility(View.VISIBLE);
//                            ((ReceiverViewHolder) holder).tv_date.setText((date));
//
//                        } else
//                            ((ReceiverViewHolder) holder).tv_date.setVisibility(View.GONE);
//
//                    } else {
//
//                        date = (messages.get(i).getDateCreated().substring(0, 10));
//                        ((ReceiverViewHolder) holder).tv_date.setVisibility(View.VISIBLE);
//                        ((ReceiverViewHolder) holder).tv_date.setText(date);
//                    }
//                }
                ((ReceiverViewHolder) holder).textViewMessage.setText(message.getMessageBody());
                ((ReceiverViewHolder) holder).textViewAuthor.setText(message.getAuthor());
                ((ReceiverViewHolder) holder).textViewDate.setText(DateFormatter.getFormattedDateFromISOString(message.getDateCreated()));
               // ((ReceiverViewHolder) holder).tv_date.setText(DateFormatter.getFormattedDateFromISOString(message.getDateCreated()));

            }
//            else if (holder instanceof DateConatiner) {
//                ((DateConatiner) holder).textViewMessage.setText(key);
//                          }
            //  TextView textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatusMessage);

//            if(holder instanceof SenderViewHolder){
//
//                ((SenderViewHolder) holder).textViewMessage.setText(value.get(key).get(position).getMessageBody());
//                ((SenderViewHolder) holder).textViewAuthor.setText(value.get(key).get(position).getAuthor());
//                ((SenderViewHolder) holder).textViewDate.setText(DateFormatter.getFormattedDateFromISOString(value.get(key).get(position).getDateCreated()));
//
//            }

        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder:  " + e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
        // return (null != hashMapArrayList ? hashMapArrayList.size() : 0);
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        // private TextView tv_message_sender;
        TextView textViewMessage, textViewAuthor, textViewDate;

        SenderViewHolder(View view) {
            super(view);
            //   tv_message_sender = view.findViewById(R.id.tv_sendChat);
            textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
            textViewAuthor = (TextView) view.findViewById(R.id.textViewAuthor);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);

        }

    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        //  private TextView tv_message_receiver;
        TextView tv_date, textViewMessage, textViewAuthor, textViewDate;

        ReceiverViewHolder(View view) {
            super(view);
            tv_date = view.findViewById(R.id.tv_date);
            textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
            textViewAuthor = (TextView) view.findViewById(R.id.textViewAuthor);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        }
    }

    class DateConatiner extends RecyclerView.ViewHolder {
        // private TextView tv_message_sender;
        TextView textViewMessage, textViewAuthor, textViewDate;

        DateConatiner(View view) {
            super(view);
            //   tv_message_sender = view.findViewById(R.id.tv_sendChat);
            textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
//            textViewAuthor = (TextView) view.findViewById(R.id.textViewAuthor);
//            textViewDate = (TextView) view.findViewById(R.id.textViewDate);

        }

    }

}
