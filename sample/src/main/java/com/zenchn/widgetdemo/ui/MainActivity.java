package com.zenchn.widgetdemo.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zenchn.widget.GridEditView.GridEditView;
import com.zenchn.widgetdemo.R;

public class MainActivity extends Activity implements GridEditView.InputListener, View.OnClickListener {

    private GridEditView gevInput;
    private TextView tvPreview;
    private ScrollView svCheck;
    private LinearLayout llCheck;
    private EditText etTextInput;
    private Button btCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gevInput = (GridEditView) findViewById(R.id.gev_input);
        tvPreview = (TextView) findViewById(R.id.tv_preview);
        svCheck = (ScrollView) findViewById(R.id.sv_check);
        llCheck = (LinearLayout) findViewById(R.id.ll_check);
        etTextInput = (EditText) findViewById(R.id.et_test_input);
        btCommit = (Button) findViewById(R.id.bt_commit);

        btCommit.setOnClickListener(this);

        gevInput.addInputListener(this);
    }

    @Override
    public void inputComplete() {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 5, 0, 5);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        boolean result = tvPreview.getText().toString().equals(gevInput.getText());

        textView.setTextColor(result ? Color.GREEN : Color.RED);

        if (result)
            textView.setText("验证一致！");
        else
            textView.setText("验证不一致！");

        llCheck.addView(textView);

        int off = llCheck.getMeasuredHeight() - svCheck.getHeight();
        if (off > 0) {
            svCheck.scrollTo(0, off);
        }

        gevInput.invalidate();

    }

    @Override
    public void deleteContent(CharSequence charSequence) {
        tvPreview.setText(charSequence);
    }

    @Override
    public void insertContent(CharSequence charSequence) {
        tvPreview.setText(charSequence);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_commit:
                gevInput.setText(etTextInput.getText().toString());
                break;
        }
    }
}
