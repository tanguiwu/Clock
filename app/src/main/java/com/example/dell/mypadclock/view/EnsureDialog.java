package com.example.dell.mypadclock.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.mypadclock.R;


/**
 * Created by qiu on 2017/4/6.
 * 确认dialog
 */

public class EnsureDialog extends BaseDialog {

    private ImageView civ_head;
    private TextView tv_title;
    private Button btn_close,btn_ok;
    private int mResId;
    private String mTitle,mBtnLeftInfo,mBtnRightInfo;
    private View.OnClickListener mLeftBtnListener,mRightBtnListener;
    //构造方法还是要的哈
    public EnsureDialog(Context context, int picResId, String title,String btnLeftInfo,
                        String btnRightInfo,
                        View.OnClickListener leftBtnListener,
                        View.OnClickListener rightBtnListener) {
        super(context);
        this.mResId=picResId;
        this.mTitle=title;
        this.mBtnLeftInfo=btnLeftInfo;
        this.mBtnRightInfo=btnRightInfo;
        this.mLeftBtnListener=leftBtnListener;
        this.mRightBtnListener=rightBtnListener;
    }

    //设置对话框的样式
    @Override
    protected int getDialogStyleId() {
        return BaseDialog.DIALOG_IOS_STYLE;
    }

    //继承于BaseDialog的方法，设置布局用的，这样对话框张啥样久随心所欲啦
    @Override
    protected View getView() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_ensure, null);
        civ_head = (ImageView) view.findViewById(R.id.civ_head);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        btn_close= (Button) view.findViewById(R.id.btn_close);
        btn_ok= (Button) view.findViewById(R.id.btn_ok);
        return view;
    }

    @Override
    protected void initData() {
        if (mResId == -1){
            civ_head.setVisibility(View.GONE);
        } else {
            civ_head.setImageResource(mResId);
        }
        tv_title.setText(mTitle);
        btn_close.setOnClickListener(mLeftBtnListener);
        btn_ok.setOnClickListener(mRightBtnListener);
        btn_close.setText(mBtnLeftInfo);
        btn_ok.setText(mBtnRightInfo);
    }
}