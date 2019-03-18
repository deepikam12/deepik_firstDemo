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
import android.widget.Toast;

import com.tobaccocessation.R;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.ItemModel;
import com.tobaccocessation.model.QuestionAnswer;

import java.util.ArrayList;

/**
 * Created by Deepika.Mishra on 9/10/2018.
 */


public class SelectionListAdapter extends RecyclerView.Adapter<SelectionListAdapter.ViewHolder> {
    private static final String TAG = "NotificationAdapter";
    ArrayList<Answers> itemList;
    String dataString;
    RadioButton selected = null;
    private Context context;
    private final LayoutInflater mLayoutInflater;
    public  static String ans_id="";
    public String answerId;
    //   private SelectionItemListener mSelectionItemListener;

    public SelectionListAdapter(Context context, ArrayList<Answers> itemModelList, String answerId) {
        this.itemList = itemModelList;
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.answerId = answerId;
        //  this.mSelectionItemListener = selectionItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.selection_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final Answers answers = itemList.get(position);
            if (!answers.getAnswer().equalsIgnoreCase("")) {
                holder.rbItem.setVisibility(View.VISIBLE);
                Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/NewsGothicBT Roman.TTF");
                holder.rbItem.setTypeface(tf);
                holder.rbItem.setText(answers.getAnswer());
            }

           /* if(!ans_id.equals("")&&(ans_id.equals(answers.ansId))) {
                holder.rbItem.setChecked(true);
                selected = holder.rbItem;
            }
            else {
                holder.rbItem.setChecked(false);
            }*/

            if(answerId != null && (answerId.equals(answers.ansId))) {
                holder.rbItem.setChecked(true);
                selected = holder.rbItem;
              //  holder.rbItem.setBackgroundColor(context.getResources().getColor(R.color.radio_button));

            }
            else {
                holder.rbItem.setChecked(false);
            }

            holder.rbItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected != null) {
                        selected.setChecked(false);
                    }


                    holder.rbItem.setChecked(true);
                //    holder.rbItem.setBackgroundColor(context.getResources().getColor(R.color.radio_button));

                    ans_id=itemList.get(position).getAnsId();
                    answerId = itemList.get(position).getAnsId();

                  //  Toast.makeText(context, itemList.get(position).getAnsId(), Toast.LENGTH_SHORT).show();

                    selected = holder.rbItem;
                }
            });

        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder:  " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton rbItem;

        ViewHolder(View view) {
            super(view);
            rbItem = view.findViewById(R.id.rb_ans);
        }

    }

}





