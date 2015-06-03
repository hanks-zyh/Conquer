/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.robobinding.binder.Binders;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.db.TaskDao;
import app.hanks.com.conquer.presentation.SearchResultPresentationModel;

/**
 * Created by Hanks on 2015/6/3.
 */
public class SearchResultActivity extends Activity {

    private SearchResultPresentationModel presentationModel;

    private List<String> list = new ArrayList<>();
    private View iv_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presentationModel = new SearchResultPresentationModel(list);
        View view = Binders.inflateAndBind(this, R.layout.activity_search_result, presentationModel);
        setContentView(view);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> d = new TaskDao(getApplication()).queryByKeyword("");
                presentationModel.setStrings(d);
            }
        }, 600);

        final EditText editText = (EditText) view.findViewById(R.id.edit_query);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                iv_clear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                presentationModel.setStrings(new TaskDao(getApplication()).queryByKeyword(s.toString()));
            }
        });

        iv_clear = findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }

    public void back(View view){
        onBackPressed();
    }
}
