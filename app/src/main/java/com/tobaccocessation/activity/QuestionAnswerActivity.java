package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.adapter.SelectionListAdapter;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.ItemModel;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.QuestionAnswer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionAnswerActivity extends AppCompatActivity implements View.OnClickListener {
    Utils utils;
    String type1, type2, type3;
    LinearLayout ll_header;
    TextView tv_next, tv_message, tv_zip, tv_questionLay2, tv_question, tv_yes, tv_no, tv_next_single, tv_mandatory, tv_restrict;
    ImageView iv_back_arrow, iv_video, iv_chat;
    String name, id, ques_id;
    View layout1, layout2, layout3;
    ArrayList<ItemModel> singleSelectionList;
    RecyclerView rv_singleChoice;
    SelectionListAdapter selectionListAdapter;
    ArrayList<QuestionAnswer> qna;
    private int pos = 0;
    EditText et_input;
    //ArrayList<String>ansList;
    Map<String, List<AnwerQuestionList>> ansList;
    static List<AnwerQuestionList> ans_id;
    AnwerQuestionList anwerQuestionList;
    ImageView iv_smileyType;
    RadioButton option1, option2, option3, option4, option5, option6;
    ;
NotificationModel notificationModel;SharedPreferences pref;
SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);
        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("ques_id");
        pos = getIntent().getIntExtra("pos", 0);
        utils = new Utils(this);
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(QuestionAnswerActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

//   select layout according to questionType;
        selectLayout();


    }

    private void selectLayout() {
        if (!(qna.size() == ans_id.size())) {
            if (qna.get(pos).getQuestion_type().equalsIgnoreCase("input"))
                addLayout1(qna.get(pos).getQuestion(), qna.get(pos).getInput_type(), qna.get(pos).getAbout(), qna.get(pos).getMandatory(), qna.get(pos).getIcon());
            else if (qna.get(pos).getQuestion_type().equalsIgnoreCase("yesnoinput"))
                addLayout2(qna.get(pos).getQuestion(), qna.get(pos).getMandatory(), qna.get(pos).getIcon());
            else if (qna.get(pos).getQuestion_type().equalsIgnoreCase("single"))
                addLayout3(qna);
        } else {
            // ansList.size();
            Intent i = new Intent(QuestionAnswerActivity.this, SubmitQuestionActivity.class);
            i.putExtra("ansList", (Serializable) ans_id);
            i.putExtra("name", name);
            i.putExtra("ques_id", id);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = pref.getBoolean("isbooleanVideo", false);
        boolean isChat = pref.getBoolean("isbooleanChat", false);
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


    private void initializeIds() {
        SelectionListAdapter.ans_id = "";
        qna = new ArrayList<>();
        ans_id = new ArrayList<AnwerQuestionList>();
        anwerQuestionList = new AnwerQuestionList();
        try {
            qna = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            if (pos < qna.size()) {
                ques_id = qna.get(pos).getId();
            }
            String isQNA = getIntent().getStringExtra("isQNA");
            if (isQNA.equalsIgnoreCase("QA"))
                ans_id = (List<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");
            else {
                ans_id = (List<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");
                //ans_id.clear();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addLayout1(String Question, String input_type, String about, String mandatory, String icon) {
        layout1 = LayoutInflater.from(this).inflate(R.layout.input_type, ll_header, false);
        tv_message = layout1.findViewById(R.id.tv_empty);
        et_input = layout1.findViewById(R.id.et_input);
        tv_zip = layout1.findViewById(R.id.tv_question_zip);
        tv_next = layout1.findViewById(R.id.tv_next);
        iv_back_arrow = layout1.findViewById(R.id.iv_back_arrow);
        iv_video=layout1.findViewById(R.id.iv_video);
        iv_chat=layout1.findViewById(R.id.iv_chat);
        tv_mandatory = layout1.findViewById(R.id.tv_madantory);
        tv_restrict = layout1.findViewById(R.id.tv_restrict);
        set_Font(tv_zip, tv_restrict, tv_next, et_input);
        iv_smileyType = layout1.findViewById(R.id.iv_smileyType);
        for (int j = 0; j < ans_id.size(); j++) {
            AnwerQuestionList anwerQuestionList = ans_id.get(j);
            if (anwerQuestionList.getQues_id().equals(ques_id)) {
                et_input.setText(anwerQuestionList.getAns());
                break;
            }
        }
        setSmiley(icon, iv_smileyType);
        if (about.equalsIgnoreCase("zip"))
            tv_restrict.setVisibility(View.VISIBLE);
        else tv_restrict.setVisibility(View.GONE);

        if (mandatory.equalsIgnoreCase("yes"))
            tv_mandatory.setVisibility(View.VISIBLE);
        else
            tv_mandatory.setVisibility(View.GONE);
        if (input_type.equalsIgnoreCase("number")) {
            et_input.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (about.equalsIgnoreCase("age")) {
                setEditTextMaxLength(3);
            } else if (about.equalsIgnoreCase("zip")) {
                setEditTextMaxLength(5);
            }

        } else if (input_type.equalsIgnoreCase("text"))
            et_input.setInputType(InputType.TYPE_CLASS_TEXT);


        tv_zip.setText(Question);

        ll_header.addView(layout1);
        tv_next.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
    }

    private void addLayout2(String question, String mandatory, String icon) {
        layout2 = LayoutInflater.from(this).inflate(R.layout.input_type_yes_no, ll_header, false);
        tv_message = layout2.findViewById(R.id.tv_empty);

        tv_yes = layout2.findViewById(R.id.tv_yes);
        tv_no = layout2.findViewById(R.id.tv_no);
        tv_questionLay2 = layout2.findViewById(R.id.tv_question_clinic_paitent);
        iv_back_arrow = layout2.findViewById(R.id.iv_back_arrow);
        iv_video=layout2.findViewById(R.id.iv_video);
        iv_chat=layout2.findViewById(R.id.iv_chat);
        tv_mandatory = layout2.findViewById(R.id.tv_madantory);
        iv_smileyType = layout2.findViewById(R.id.iv_smileyType);

        setSmiley(icon, iv_smileyType);

        set_Font1(tv_yes, tv_no, tv_questionLay2);
        for (int j = 0; j < ans_id.size(); j++) {
            AnwerQuestionList anwerQuestionList = ans_id.get(j);
            if (anwerQuestionList.getQues_id().equals(ques_id)) {
                if (anwerQuestionList.getAns().equals("yes")) {
                    tv_yes.setBackgroundColor(getResources().getColor(R.color.grey));
                    tv_no.setBackgroundColor(getResources().getColor(R.color.light_blue));
                } else {
                    tv_no.setBackgroundColor(getResources().getColor(R.color.grey));
                    tv_yes.setBackgroundColor(getResources().getColor(R.color.light_blue));
                }
                break;
            }
        }
        if (mandatory.equalsIgnoreCase("yes"))
            tv_mandatory.setVisibility(View.VISIBLE);
        else
            tv_mandatory.setVisibility(View.GONE);
        tv_questionLay2.setText(question);
        ll_header.addView(layout2);
        tv_yes.setOnClickListener(this);
        tv_no.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);

    }

    private void addLayout3(ArrayList<QuestionAnswer> question) {
        layout3 = LayoutInflater.from(this).inflate(R.layout.input_type_single_selection, ll_header, false);
        tv_message = layout3.findViewById(R.id.tv_empty);

        tv_next_single = layout3.findViewById(R.id.tv_next_single);
        rv_singleChoice = layout3.findViewById(R.id.rv_singleChoice);
        iv_back_arrow = layout3.findViewById(R.id.iv_back_arrow);
        iv_video=layout3.findViewById(R.id.iv_video);
        iv_chat=layout3.findViewById(R.id.iv_chat);
        tv_mandatory = layout3.findViewById(R.id.tv_madantory);
        tv_question = layout3.findViewById(R.id.tv_question);
        tv_question.setText(question.get(pos).getQuestion());
        iv_smileyType = layout3.findViewById(R.id.iv_smileyType);

        set_Font2(tv_question);
        setSmiley(question.get(pos).getIcon(), iv_smileyType);

        String selectedAnswerId = null;
        for (int j = 0; j < ans_id.size(); j++) {
            AnwerQuestionList anwerQuestionList = ans_id.get(j);
            if (anwerQuestionList.getQues_id().equals(ques_id)) {
                SelectionListAdapter.ans_id = anwerQuestionList.getAns_id();
                selectedAnswerId = anwerQuestionList.getAns_id();
                break;
            }
        }
        if (question.get(pos).getMandatory().equalsIgnoreCase("yes"))
            tv_mandatory.setVisibility(View.VISIBLE);
        else
            tv_mandatory.setVisibility(View.GONE);
        iv_back_arrow.setOnClickListener(this);
        tv_next_single.setOnClickListener(this);
        ll_header.addView(layout3);

        singleSelectionList = new ArrayList<>();
        rv_singleChoice.setLayoutManager(new LinearLayoutManager(QuestionAnswerActivity.this));
        selectionListAdapter = new SelectionListAdapter(QuestionAnswerActivity.this, question.get(pos).getAnswersList(), selectedAnswerId);
        rv_singleChoice.setAdapter(selectionListAdapter);
    }

    public void setSmiley(String icon, ImageView iv_smileyType) {
        if (icon.equalsIgnoreCase("naviLaughing")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_laughing);
        } else if (icon.equalsIgnoreCase("naviSmile")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_smile);

        } else if (icon.equalsIgnoreCase("naviSmileTeeth")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_smile_teeth);

        } else if (icon.equalsIgnoreCase("naviTalking")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_talking);

        } else if (icon.equalsIgnoreCase("naviThinking")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_thinking);

        }
    }

    public void set_Font(TextView tv_ques, TextView tv_restrict, TextView tv_next, EditText et_input) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");

        tv_ques.setTypeface(tf);
        tv_restrict.setTypeface(tf);
        et_input.setTypeface(tf);
        tv_next.setTypeface(tf1);
    }

    public void set_Font1(TextView tv_yes, TextView tv_no, TextView tv_ques) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");

        tv_ques.setTypeface(tf);
        tv_yes.setTypeface(tf1);
        tv_no.setTypeface(tf1);

    }

    public void set_Font2(TextView tv_ques) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        tv_ques.setTypeface(tf);

    }

    public void setEditTextMaxLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        et_input.setFilters(filterArray);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                Intent i;
                validateInput();
                break;

            case R.id.tv_yes:
                tv_yes.setBackgroundColor(getResources().getColor(R.color.grey));
                tv_no.setBackgroundColor(getResources().getColor(R.color.light_blue));
                boolean flag = false;
                for (int j = 0; j < ans_id.size(); j++) {
                    AnwerQuestionList anwerQuestionList = ans_id.get(j);
                    if (anwerQuestionList.getQues_id().equals(ques_id)) {
                        anwerQuestionList.setAns_id(SelectionListAdapter.ans_id);
                        anwerQuestionList.setAns("yes");
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    anwerQuestionList.setQues_id(qna.get(pos).getId());
                    anwerQuestionList.setAns("yes");
                    anwerQuestionList.setAns_id(SelectionListAdapter.ans_id);
                    ans_id.add(anwerQuestionList);
                }


                int position = pos + 1;
                i = new Intent(QuestionAnswerActivity.this, QuestionAnswerActivity.class);
                i.putExtra("questionList", qna);
                i.putExtra("ansList", (Serializable) ans_id);
                i.putExtra("name", name);
                i.putExtra("ques_id", id);
                i.putExtra("isQNA", "QA");
                i.putExtra("pos", position);

                // pos = pos - 1;

                startActivity(i);
                break;

            case R.id.tv_no:

                tv_no.setBackgroundColor(getResources().getColor(R.color.grey));
                tv_yes.setBackgroundColor(getResources().getColor(R.color.light_blue));
                flag = false;
                for (int j = 0; j < ans_id.size(); j++) {
                    AnwerQuestionList anwerQuestionList = ans_id.get(j);
                    if (anwerQuestionList.getQues_id().equals(ques_id)) {
                        anwerQuestionList.setAns_id(SelectionListAdapter.ans_id);
                        anwerQuestionList.setAns("no");
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    anwerQuestionList.setQues_id(qna.get(pos).getId());
                    anwerQuestionList.setAns("no");
                    anwerQuestionList.setAns_id(SelectionListAdapter.ans_id);
                    ans_id.add(anwerQuestionList);
                }


                position = pos + 1;
                i = new Intent(QuestionAnswerActivity.this, QuestionAnswerActivity.class);
                i.putExtra("questionList", qna);
                i.putExtra("ansList", (Serializable) ans_id);
                i.putExtra("name", name);
                i.putExtra("ques_id", id);
                i.putExtra("isQNA", "QA");
                i.putExtra("pos", position);
                //  pos = pos - 1;
                startActivity(i);
                break;


            case R.id.tv_next_single:
                position = pos + 1;
                for (int j = 0; j < ans_id.size(); j++) {
                    AnwerQuestionList anwerQuestionList = ans_id.get(j);
                    if (anwerQuestionList.getQues_id().equals(ques_id)) {
                        SelectionListAdapter.ans_id = anwerQuestionList.getAns_id();
                        break;
                    }
                }


                //if (!SelectionListAdapter.ans_id.equalsIgnoreCase("")) {
                if (qna.get(pos).getMandatory().equalsIgnoreCase("yes")) {
                    if (selectionListAdapter.answerId != null) {
                        flag = false;
                        for (int j = 0; j < ans_id.size(); j++) {
                            AnwerQuestionList anwerQuestionList = ans_id.get(j);
                            if (anwerQuestionList.getQues_id().equals(ques_id)) {
                                anwerQuestionList.setAns_id(selectionListAdapter.answerId);
                                anwerQuestionList.setAns("");
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            anwerQuestionList.setQues_id(qna.get(pos).getId());
                            anwerQuestionList.setAns("");
                            anwerQuestionList.setAns_id(selectionListAdapter.answerId);
                            ans_id.add(anwerQuestionList);
                        }


                        i = new Intent(QuestionAnswerActivity.this, QuestionAnswerActivity.class);
                        i.putExtra("questionList", qna);
                        i.putExtra("ansList", (Serializable) ans_id);
                        i.putExtra("name", name);
                        i.putExtra("ques_id", id);
                        i.putExtra("isQNA", "QA");
                        i.putExtra("pos", position);
                        startActivity(i);
                    } else {
                        utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.ans_val));
                        //Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.ans_val), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    i = new Intent(QuestionAnswerActivity.this, QuestionAnswerActivity.class);
                    i.putExtra("questionList", qna);
                    i.putExtra("ansList", (Serializable) ans_id);
                    i.putExtra("name", name);
                    i.putExtra("ques_id", id);
                    i.putExtra("isQNA", "QA");
                    i.putExtra("pos", position);
                    startActivity(i);
                }
                break;

            case R.id.iv_back_arrow:
                if (pos == 0) {
                    Question2Activity.ans_id = ans_id;
                    Question1Activity.ans_id = ans_id;
                    Question1_1Activity.ans_id = ans_id;

                }
                finish();
                break;

            case R.id.iv_video:
                i = new Intent(QuestionAnswerActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
        }
    }

    private void validateInput() {
        if (qna.get(pos).getAbout().equalsIgnoreCase("zip")) {
            if (qna.get(pos).getMandatory().equalsIgnoreCase("yes")) {
                if (!et_input.getText().toString().equalsIgnoreCase("")) {


                    if (et_input.getText().toString().length() > 5 || et_input.getText().toString().length() < 5) {
                        utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.val_zip));

                        //    Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.val_zip), Toast.LENGTH_SHORT).show();

                    } else {
                        loadNextQuestion();

                    }

                } else
                    utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.empty_zip));
            } else {

                if (et_input.getText().toString().equalsIgnoreCase("")) {
                    loadNextQuestion();
                } else {

                    if (et_input.getText().toString().length() > 5 || et_input.getText().toString().length() < 5) {
                        utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.val_zip));

                        //    Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.val_zip), Toast.LENGTH_SHORT).show();

                    } else {
                        loadNextQuestion();

                    }
                }

            }
            //  Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.empty_zip), Toast.LENGTH_SHORT).show();
        } else if (qna.get(pos).getAbout().equalsIgnoreCase("age")) {
            if (qna.get(pos).getMandatory().equalsIgnoreCase("yes")) {
                if (!et_input.getText().toString().equalsIgnoreCase("")) {

                    if (!(Integer.parseInt(et_input.getText().toString().trim()) > 1 && Integer.parseInt(et_input.getText().toString().trim()) <= 110)) {
                        utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.val_age));

                        //  Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.val_age), Toast.LENGTH_SHORT).show();
                    } else {
                        loadNextQuestion();
                    }

                } else {
                    utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.empty_age));

                    //  Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.empty_age), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (et_input.getText().toString().equalsIgnoreCase("")) {
                    loadNextQuestion();
                } else {
                    if (!(Integer.parseInt(et_input.getText().toString().trim()) > 1 && Integer.parseInt(et_input.getText().toString().trim()) <= 110)) {
                        utils.showAlertText(QuestionAnswerActivity.this, tv_message, getResources().getString(R.string.val_age));

                        //  Toast.makeText(QuestionAnswerActivity.this, getResources().getString(R.string.val_age), Toast.LENGTH_SHORT).show();
                    } else {
                        loadNextQuestion();
                    }
                }
                //  loadNextQuestion();
            }
        }

    }


    private void loadNextQuestion() {
        boolean flag = false;
        for (int j = 0; j < ans_id.size(); j++) {
            AnwerQuestionList anwerQuestionList = ans_id.get(j);
            if (anwerQuestionList.getQues_id().equals(ques_id)) {
                anwerQuestionList.setAns_id("");
                anwerQuestionList.setAns(et_input.getText().toString());
                flag = true;
                break;
            }
        }

        if (!flag) {
            anwerQuestionList.setQues_id(qna.get(pos).getId());
            anwerQuestionList.setAns(et_input.getText().toString());
            anwerQuestionList.setAns_id(SelectionListAdapter.ans_id);
            ans_id.add(anwerQuestionList);
        }

        int position = pos + 1;
        Intent i = new Intent(QuestionAnswerActivity.this, QuestionAnswerActivity.class);
        i.putExtra("questionList", qna);
        i.putExtra("isQNA", "QA");
        i.putExtra("name", name);
        i.putExtra("ques_id", id);
        i.putExtra("pos", position);
        i.putExtra("ansList", (Serializable) ans_id);
        startActivity(i);

    }
}
