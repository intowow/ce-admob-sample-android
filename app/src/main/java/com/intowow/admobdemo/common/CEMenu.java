package com.intowow.admobdemo.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.intowow.admobdemo.R;

public class CEMenu extends RelativeLayout {

    private ScrollView mScrollView = null;
    private LinearLayout mLinearLayout = null;

    public CEMenu(Context context, LayoutManager layoutMgr) {
        super(context);
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        setLayoutParams(fp);

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        params.topMargin = layoutMgr.getMetric(LayoutManager.LayoutID.MENU_TOP_MARGIN);
        params.bottomMargin = layoutMgr.getMetric(LayoutManager.LayoutID.MENU_BUTTOM_MARGIN);
        mScrollView = new ScrollView(context);
        mScrollView.setLayoutParams(params);

        fp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setLayoutParams(fp);
        mScrollView.addView(mLinearLayout);
        addView(mScrollView);

        setBackgroundResource(R.drawable.bg);

        //	disable auto show the keyboard
        //
        if (context instanceof Activity) {
            ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

    }

    public void addButton(CEButton button) {
        mLinearLayout.addView(button);
    }

    public void addCheckBox(CECheckBox checkBox) {
        mLinearLayout.addView(checkBox);
    }

    public void addEditText(CEEditText editText) {
        mLinearLayout.addView(editText);
    }

    public static class CECheckBox extends CheckBox {

        public CECheckBox(Context context, final boolean defaultValue, final SharedPreferences preferences, String text, final String preferenceKey) {
            super(context);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.bottomMargin = (int) getResources().getDimension(R.dimen.marginbottom_ad_btn);
            params.leftMargin = (int) getResources().getDimension(R.dimen.marginleft_ad_btn);
            params.rightMargin = (int) getResources().getDimension(R.dimen.marginright_ad_btn);
            params.topMargin = (int) getResources().getDimension(R.dimen.margintop_ad_btn);
            setLayoutParams(params);

            setText(text);
            setChecked(defaultValue);

            setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    preferences.edit().putBoolean(preferenceKey, checked).commit();
                }
            });


        }
    }

    public static class CEButton extends Button{

        private Class<?> mActivityClass = null;

        public CEButton(Context context, String text, Class<?> activityClass) {
            super(context);
            mActivityClass = activityClass;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.bottomMargin = (int) getResources().getDimension(R.dimen.marginbottom_ad_btn);
            params.leftMargin = (int) getResources().getDimension(R.dimen.marginleft_ad_btn);
            params.rightMargin = (int) getResources().getDimension(R.dimen.marginright_ad_btn);
            params.topMargin = (int) getResources().getDimension(R.dimen.margintop_ad_btn);
            setLayoutParams(params);

            setText(text);
            setTextColor(Color.WHITE);
            setTextSize(16);

            setBackgroundResource(R.drawable.btn_ad_effect);

            setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(mActivityClass != null) {
                        Context context = CEButton.this.getContext();
                        Intent intent = new Intent();
                        intent.setClass(context, mActivityClass);
                        context.startActivity(intent);
                    }
                }

            });
        }

    }

    public static class CEEditText extends LinearLayout {

        public CEEditText(Context context, final int defaultValue, final SharedPreferences preferences, final String text, final String preferenceKey) {
            super(context);
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            );
            setLayoutParams(params);
            setOrientation(LinearLayout.HORIZONTAL);
            setWeightSum(2.0f);

            EditTextInternal editText = new EditTextInternal(context, defaultValue, preferences, preferenceKey);

            addView(editText);

            TextView textView = new TextView(context);
            LayoutParams textViewParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            );
            textViewParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(textViewParams);
            textView.setText(text);

            addView(textView);
        }

        class EditTextInternal extends EditText {

            public EditTextInternal(Context context, final int defaultValue, final SharedPreferences preferences, final String preferenceKey) {
                super(context);

                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT,
                        1.5f
                );

                params.bottomMargin = (int) getResources().getDimension(R.dimen.marginbottom_edittext);
                params.leftMargin = (int) getResources().getDimension(R.dimen.marginleft_edittext);
                params.rightMargin = (int) getResources().getDimension(R.dimen.marginright_edittext);
                params.topMargin = (int) getResources().getDimension(R.dimen.margintop_edittext);
                setLayoutParams(params);

                setInputType(InputType.TYPE_CLASS_NUMBER);
                setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

                setText(String.valueOf(defaultValue));

                setOnEditorActionListener(new OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        preferences.edit().putInt(preferenceKey, Integer.valueOf(textView.getText().toString())).commit();
                        return true;
                    }
                });
            }
        }
    }
}
