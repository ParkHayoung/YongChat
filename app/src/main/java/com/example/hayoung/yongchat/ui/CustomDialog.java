package com.example.hayoung.yongchat.ui;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.hayoung.yongchat.R;

public class CustomDialog extends Dialog implements View.OnClickListener {
    private EditText emailInput;
    private Button searchBtn;
    private Button cancelBtn;

    private CustomDialogClickListener listener;

    public interface CustomDialogClickListener {
        void onDialogSearchButtonClick(String email);
        void onDialogCancelButtonClick();
    }

    public CustomDialog(Activity activity, CustomDialogClickListener listener) {
        super(activity);

        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        searchBtn = (Button) findViewById(R.id.ok_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        searchBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        emailInput = (EditText) findViewById(R.id.dialog_title_text_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                listener.onDialogSearchButtonClick(emailInput.getText().toString());
                break;
            case R.id.cancel_btn:
                listener.onDialogCancelButtonClick();
                break;
            default:
                break;
        }
        dismiss();
    }
}