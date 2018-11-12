package com.simple.mail.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.simple.mail.entity.Person;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class WriteMailUtils {
   /* private static final String TAG = "WriteMailUtils";
    public static ArrayList<Person> tos;
    public static ArrayList<Person> ccs;
    public static ArrayList<Person> bccs;
    public static int[] selecteds;
    public static EditText etTo;
    public static EditText etCc;
    public static EditText etBcc;
    public static TextView tvCc;
    public static LinearLayout llBcc;
    public static EditText etSubject;
    public static MyGridView myGridView;
    private Activity activity

    public static void initWriteMailUtils(Activity activity, ArrayList<Person> tos1, ArrayList<Person> ccs1, ArrayList<Person> bccs1,
                                          int[] selecteds1, EditText etTo1, EditText etCc1, EditText etBcc1, TextView tvCc1, LinearLayout llBcc1,
                                          EditText etSubject1, MyGridView myGridView1) {
        activity = activity;
        tos = tos1;
        ccs = ccs1;
        bccs = bccs1;
        selecteds = selecteds1;
        etTo = etTo1;
        etCc = etCc1;
        etBcc = etBcc1;
        tvCc = tvCc1;
        llBcc = llBcc1;
        myGridView = myGridView1;
        etSubject = etSubject1;
    }



    *//**
     * 获得签名加空白
     *//*
    public static String getSignWithSpace() {
        return "<br><br><br><br><hr size='0.5' color='#E4E4E4'/><span style='color:#666666;font-size: 14px;'>" + getSign() + "</span>";
    }

    *//**
     * 设置附件的myGridView
     *
     * @param attachs 附件的数据源
     *//*
    public static void setMyGridView(final ArrayList<Attach> attachs, AttachAdapter attachAdapter) {
        attachs.add(new Attach());
        myGridView.setAdapter(attachAdapter);
        if (attachs.size() < 2) {
            myGridView.setVisibility(View.GONE);
        } else {
            myGridView.setVisibility(View.VISIBLE);
        }
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == attachs.size() - 1) {//添加附件
                    PopupUtils.showPopupWindowAddAttach(context);
                }
            }
        });
    }

    *//**
     * etTo,etCc,etBcc的各种监听
     *//*
    public static void setEditTextListener(final EditText et, final ArrayList<Person> list, final int index) {
        //得到EditText的父控件，以后会在里面添加textview
        final FlowLayout flow = (FlowLayout) et.getParent();
        //写邮件界面，收件人，抄送人，密送人栏目，当该处失去焦点，收起flow的时候，显示出的textview
        final TextView textView = (TextView) ((RelativeLayout) flow.getParent()).getChildAt(1);
        //得到EditText后面的添加按钮
        final View add = ((RelativeLayout) flow.getParent().getParent()).getChildAt(1);

        //EditText焦点变化的各种监听
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // “收件人”栏edittext获取焦点和失去焦点的时候的监听。
                if (hasFocus) {
                    //add.setVisibility(View.VISIBLE);
                    if (selecteds[index] < 0) {//如果selecteds[index]>=0,此时获得焦点的栏目有被选中的联系人。
                        setAllTextViewNoSelected();
                    }
                } else {
                    //add.setVisibility(View.INVISIBLE);
                    String stringEt = StringManager.getStringByTv(et);
                    //  通过edittext中的有效内容来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
                    setFlowByEditTextString(et, flow, list, stringEt, index);

                }

                switch (v.getId()) {
                    case R.id.et_cc_write_mail:// 抄送
                        if (hasFocus) {
                            llBcc.setVisibility(View.VISIBLE);
                            tvCc.setText(StringManager.getString(R.string.copy_to));
                        }
                    case R.id.et_bcc_write_mail:// 密送
                        // 如果抄送框,密送框都没有文字,且都是去焦点,则隐藏密送框
                        if (!etCc.isFocused() && !etBcc.isFocused() && ccs.isEmpty() && bccs.isEmpty()) {
                            llBcc.setVisibility(View.GONE);
                            tvCc.setText(StringManager.getString(R.string.copy_to_blind_carbon_copy));
                        }
                        break;
                }
            }
        });

        *//**
         *写邮件界面，收件人，抄送人，密送人栏目，当该处失去焦点，收起flow的时候，显示出的textview
         * 点击时，隐藏三个栏目的textview,放开flow显示。
         *//*
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlowGone(etTo);
                setFlowGone(etCc);
                setFlowGone(etBcc);
                getFocus(et);
            }

            *//**
             *显示flow，隐藏textView
             *//*
            private void setFlowGone(EditText et) {
                FlowLayout flow = (FlowLayout) et.getParent();
                //flow的兄弟控件textview，当该栏收缩是显示联系人。
                TextView textView = (TextView) ((RelativeLayout) flow.getParent()).getChildAt(1);
                flow.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }
        });

        //监听软键盘的删除键
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String string = StringManager.getStringByTv(et);
                    if (TextUtils.isEmpty(string)) {//et中没有字符可以删除，则删除一个联系人
                        if (selecteds[index] < 0) {//selecteds[index] < 0证明没有选中任何联系人
                            if (list.size() > 0) {//没有选中任何联系人，list.size() > 0证明已经有联系人，可以删除最后一个联系人
                                selecteds[index] = list.size() - 1;
                                TextView childText = (TextView) flow.getChildAt(flow.getChildCount() - 2);
                                setSelectedTextView((Person) childText.getTag(), childText);//设置被选中的textview
                            }
                        } else {//selecteds[index] > 0此时有选中的联系人
                            flow.removeViewAt(selecteds[index]);
                            list.remove(selecteds[index]);
                            if (list.size() > 0) {
                                StringManager.removeComma((TextView) flow.getChildAt(flow.getChildCount() - 2));
                            }
                            selecteds[index] = -1;
                        }
                    }
                }
                return false;
            }
        });

        //EditText里面内容的变化的各种监听
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                PopupUtils.showPopupWindowContactsHint(context, et, flow, list, index);
                //  通过edittext中的有效内容来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
                String string = s.toString();
                if (string.equals("\n") || string.equals(" ")) {//不可以以换行或者空格开头
                    et.setText("");
                }
                if (string.length() > 1 && (string.endsWith(";") || string.endsWith("\n"))) {
                    setFlowByEditTextString(et, flow, list, string.substring(0, string.length() - 1), index);
                }
            }
        });

        //点击“收件人”“抄送”“密送”栏的空白区域的时候，让里面的edittext获取焦点。
        flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFocus(et);//et获得焦点，并显示软键盘
            }
        });

        //点击“收件人”“抄送”“密送”栏后面的添加按钮
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  通过edittext中的有效内容来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
                setFlowByEditTextString(et, flow, list, StringManager.getStringByTv(et), index);
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                bundle.putParcelableArrayList("list", list);
                //context.startActivity(SelectContacts1Activity.class, bundle);
            }
        });
    }


    *//**
     * et获得焦点，并显示软键盘
     *//*
    public static void getFocus(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();//获取焦点 光标出现
        et.setCursorVisible(true);
        InputUtills.show(context, et);
    }

    *//**
     * 一次“收件人”“抄送”“密送”栏的edittext输入结束之后，通过edittext中的有效内容
     * 来判断“收件人”“抄送”“密送”栏中是否要添加一个textview。
     *//*
    public static void setFlowByEditTextString(EditText et, final FlowLayout flow, final ArrayList<Person> list, String stringEt, final int index) {
        //如果edittext里面什么也没有，就不做什么操作,直接返回
        if (TextUtils.isEmpty(stringEt)) {
            return;
        }
        //将stringEt作为关键字，在数据库中通过邮件和名字查询，看是否已经存在符合条件的contacts
        List<Person> personList = Jna.getInstance().getPerson(stringEt);
        Person person = personList.isEmpty() ? null : personList.get(0);
        if (person == null) {
            person = new MailContact(stringEt, stringEt);
        }
        //如果list中包含contacts则什么都不操作。
        if (StringManager.containsContacts(person, list)) {
            et.setText("");
            return;
        }
        //将联系人添加到list里面
        list.add(person);
        addFlowChild(et, flow, list, index, person);
    }

    *//**
     * 实例化一个textviev。里面放contacts，加入到flow，里面
     *//*
    public static void addFlowChild(final EditText et, FlowLayout flow, final ArrayList<Person> list, final int index, final Person person) {
        //如果edittext里面有内容，则new 一个TextView，并对TextView设置
        final TextView textView = new TextView(context);
        // 第一个参数为宽的设置，第二个参数为高的设置。
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);//居中
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_size_normal));
        setTextView(person, textView);
        textView.setText(person.name);
        textView.setTag(person);
        et.setText(""); //将后面的edittext里的内容设为空
        int childCount = flow.getChildCount();
        if (childCount > 1) {//如果再edittext前面有textview，则在前面的textview里面文字后面加“、”
            TextView childTextView = (TextView) flow.getChildAt(childCount - 2);
            StringManager.addComma(childTextView);
        }
        flow.addView(textView, childCount - 1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlowLayout parent = (FlowLayout) textView.getParent();
                EditText etChild = (EditText) parent.getChildAt(parent.getChildCount() - 1);
                getFocus(etChild); //et获得焦点，并显示软键盘
                etChild.setCursorVisible(false);//不显示etChild中的光标
                StringManager.removeComma(textView);//去逗号
                Person personSelected = (Person) textView.getTag();
                int selected = getContactsIndex(list, personSelected);  // 通过textview上的文字判断，这是list中的第几个联系人
                if (selecteds[index] != selected) {

                    setAllTextViewNoSelected();//将“收件人”“抄送”“密送”栏中所有的textview设置为未选中状态
                    selecteds[index] = selected;
                    setSelectedTextView(personSelected, textView);//设置被选中的textview
                }
            }
        });
    }

    *//**
     * 设置被选中的textview
     *//*
    public static void setSelectedTextView(Person contactsSelected, TextView textView) {
        StringManager.removeComma(textView);//去逗号
        textView.setPadding(ScreenUtils.dip2px(context, 5), 0, ScreenUtils.dip2px(context, 5), 0);
        if (isMail(contactsSelected)) {
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_space_blue_litter));
        } else {
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_space_red_litter));
        }
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    *//**
     * 如果list中包含contacts则返回contacts的下标，如果不在则返回-1。
     *//*
    public static int getContactsIndex(ArrayList<Person> list, Person person) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).email.equals(person.email)) {
                return i;
            }
        }
        return -1;
    }

    *//**
     * 将“收件人”“抄送”“密送”栏中所有的textview设置为未选中状态
     *//*
    public static void setAllTextViewNoSelected() {
        setChildTextView(etTo, tos);
        setChildTextView(etCc, ccs);
        setChildTextView(etBcc, bccs);
        selecteds[0] = -1;
        selecteds[1] = -1;
        selecteds[2] = -1;
    }

    *//**
     * 将“收件人”“抄送”“密送”栏中所有的textview设置为未选中状态
     *//*
    public static void setChildTextView(EditText et, ArrayList<Person> list) {
        for (int i = 0; i < list.size(); i++) {
            Person person = list.get(i);
            TextView child = (TextView) ((FlowLayout) et.getParent()).getChildAt(i);
            if (child == null) break;
            StringManager.addComma(child);
            if (i == list.size() - 1) {
                StringManager.removeComma(child);
            }
            setTextView(person, child);
        }
    }



    *//**
     * 判断该联系人的邮箱是否有效，如果没有效，这说明用户输入的是错的邮箱地址
     *//*
    private static boolean isMail(Person person) {
        if (person.email.contains("@")) {
            return true;
        }
        return false;
    }

    *//**
     * 从selectContactsActivity界面返回之后，根据返回值来更新相应的Flow
     *//*
    public static void updateFlowFromSelectContacts(Intent data) {
        List<Person> receiverList = data.getParcelableArrayListExtra("data");
        for (int i = 0; i < receiverList.size() - 1; i++) {
            for (int j = receiverList.size() - 1; j > i; j--) {
                if (receiverList.get(j).email.equals(receiverList.get(i).email)) {
                    receiverList.remove(j);
                }
            }
        }
        int index = data.getIntExtra("index", -1);
        switch (index) {
            case 0:
                tos.clear();
                tos.addAll(receiverList);
                updataFlow(etTo, index, tos);
                break;
            case 1:
                ccs.clear();
                ccs.addAll(receiverList);
                updataFlow(etCc, index, ccs);
                break;
            case 2:
                bccs.clear();
                bccs.addAll(receiverList);
                updataFlow(etBcc, index, bccs);
                break;
        }
    }

    *//**
     * 调用一：从selectContactsActivity界面返回之后，根据返回值来更新相应的Flow
     * 调用二：从MailFragment界面点击 item进入WriteMailActivity界面，根据传过来得时，填充收件人，抄送人，密送人
     *//*
    public static void updataFlow(EditText et, int index, ArrayList<Person> list) {
        //得到EditText的父控件，以后会在里面添加textview
        final FlowLayout flow = (FlowLayout) et.getParent();
        for (int i = flow.getChildCount() - 2; i >= 0; i--) {
            flow.removeViewAt(i);
        }
        for (int j = 0; j < list.size(); j++) {
            addFlowChild(et, flow, list, index, list.get(j));
        }
    }

    *//**
     * 调用系统相机照相。
     *//*
    public static void photo(WriteMailActivity activity, int requestCode) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dirFile = new File(AppConfig.EMAIL_PHOTO_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dirFile, "img_" + TimeManager.getIMGTime() + ".jpg");
        WriteMailActivity.takePhotoPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(openCameraIntent, requestCode);
    }

    *//**
     * 将需要添加的附件添加的附件的数据源中
     *
     * @param path 附件的路径
     *//*
    public static void addAttach(ArrayList<Attach> attachs, String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            Attach attach = new Attach(file.getName(), file.getAbsolutePath(), Formatter.formatFileSize(context, file.length()));
            attachs.add(attach);
        }
    }

    *//**
     * etSubject或者richEditor获得焦点的时候，flow收缩，用textview显示。
     *//*
    public static void setSubject(EditText et) {
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setFlowVisible(etTo, tos);
                    setFlowVisible(etCc, ccs);
                    setFlowVisible(etBcc, bccs);
                }
            }
        });
    }

    *//**
     * etSubject或者richEditor获得焦点的时候，flow收缩，用textview显示。
     *//*
    public static void setRichEditor(WebView webView, final ImageView insertImg) {
        webView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setAllFlowGone();
                    //etMailContent得到焦点，显示添加图片按钮，失去焦点隐藏添加图片按钮
                    insertImg.setVisibility(View.VISIBLE);
                } else {
                    insertImg.setVisibility(View.GONE);
                }
            }
        });
    }

    *//**
     * 设置隐藏所有的flow,显示textview
     *//*
    public static void setAllFlowGone() {
        setFlowVisible(etTo, tos);
        setFlowVisible(etCc, ccs);
        setFlowVisible(etBcc, bccs);
    }


    //设置显示textview，隐藏flow
    public static void setFlowVisible(EditText et, ArrayList<Person> list) {
        FlowLayout flow = (FlowLayout) et.getParent();
        //flow的兄弟控件textview，当该栏收缩是显示联系人。
        TextView textView = (TextView) ((RelativeLayout) flow.getParent()).getChildAt(1);
        flow.setVisibility(View.GONE);
        setTextView(list, textView);
        textView.setVisibility(View.VISIBLE);
    }

    //将list里的联系人设置到textview中去，单行显示，多余的部分用“...等a人”
    public static void setTextView(ArrayList<Person> list, TextView textview) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).name + "、");
        }
        String content = sb.toString();
        content = StringManager.removeComma(content);//去逗号
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textview.measure(width, height);
        int contactNameTextsize = context.getResources().getDimensionPixelSize(R.dimen.text_size_normal);
        int mLimitContactsLength = ScreenUtils.getFlowWidth(context, textview);
        Paint paint = new Paint();
        paint.setTextSize(contactNameTextsize);
        float pointWidth = paint.measureText(" ...等" + list.size() + "人");
        if (TextUtils.isEmpty(content)) {
            textview.setText("");
            return;
        }
        char[] textCharArray = content.toCharArray();
        //已绘的宽度
        float drawedWidth = 0;
        float charWidth;
        for (int i = 0; i < textCharArray.length; i++) {
            charWidth = paint.measureText(textCharArray, i, 1);
            if (mLimitContactsLength - drawedWidth - pointWidth < charWidth) {
                textview.setText(content.subSequence(0, i) + " ...等" + list.size() + "人");
                return;
            } else {
                drawedWidth += charWidth;
            }
        }
        textview.setText(content);
    }

*/
}
