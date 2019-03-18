package com.tobaccocessation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tobaccocessation.R;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.QuestionAnswer;
import com.tobaccocessation.model.SuggestionList;

import java.util.ArrayList;

/**
 * Created by Deepika.Mishra on 1/3/2019.
 */

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
    private static final String TAG = "NotificationAdapter";
    ArrayList<SuggestionList> itemList;
    String dataString;
    RadioButton selected = null;
    private Context context;
    private final LayoutInflater mLayoutInflater;
    public static String ans_id = "";
    public String answerId;
    //   private SelectionItemListener mSelectionItemListener;

    public SuggestionAdapter(Context context, ArrayList<SuggestionList> itemModelList) {
        this.itemList = itemModelList;
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.answerId = answerId;
        //  this.mSelectionItemListener = selectionItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.suggestion_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final SuggestionList suggestionList = itemList.get(position);

            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "fonts/NewsGothicBT Bold.TTF");
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/NewsGothicBT Roman.TTF");

//            for(int i=1;i<itemList.size();i++) {
//                holder.tv_number.setText(i + ".");
//            }
            holder.tv_number.setText(position+1 + ".");

            holder.tv_title.setText( suggestionList.getTitle());
            holder.tv_content.setText(( suggestionList.getContent()));
            holder.tv_content.setTypeface(tf);
            holder.tv_title.setTypeface(tf1);
            holder.tv_number.setTypeface(tf1);
           /* if(!ans_id.equals("")&&(ans_id.equals(answers.ansId))) {
                holder.rbItem.setChecked(true);
                selected = holder.rbItem;
            }
            else {
                holder.rbItem.setChecked(false);
            }*/


        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder:  " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_number, tv_title, tv_content;

        ViewHolder(View view) {
            super(view);
            tv_content = view.findViewById(R.id.tv_content);
            tv_title = view.findViewById(R.id.tv_title);
            tv_number = view.findViewById(R.id.tv_number);

        }

    }
}