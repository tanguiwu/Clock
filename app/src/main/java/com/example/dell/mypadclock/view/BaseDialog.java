package com.example.dell.mypadclock.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.dell.mypadclock.R;

/**
 * Created by qiu on 2017/4/6.
 * dialog的基类
 */

public abstract class BaseDialog {
    //这些属性，Context 是肯定要的，基本对话框要用它
    protected Context context;
    private Display display;//这个设置显示属性用的
    private Dialog dialog;//自定义Dialog，Dialog还是要有一个的吧
    private WindowManager.LayoutParams lp;
    private int percentageH = 4;
    private int percentageW = 2;
    protected static int DIALOG_COMMON_STYLE = R.style.common_dialog_style;
    protected static int DIALOG_IOS_STYLE = R.style.dialog_ios_style;

    //对话框布局的样式ID (通过这个抽象方法，我们可以给不同的对话框设置不同样式主题)
    protected abstract int getDialogStyleId();

    //构建对话框的方法(都说了是不同的对话框，布局什么的肯定是不一样的)
    protected abstract View getView();

    protected abstract void initData();

    //构造方法 来实现 最基本的对话框
    public BaseDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        //在这里初始化 基础对话框s
        if (getDialogStyleId() == 0) {
            dialog = new Dialog(context, DIALOG_COMMON_STYLE);
        } else {
            dialog = new Dialog(context, getDialogStyleId());
        }
        // 调整dialog背景大小
//        getView().setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(getView());
        //隐藏系统输入盘
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
// 设置window属性
        lp = dialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0.6f; // 去背景遮盖
        lp.alpha = 1.0f;
        int[] wh = initWithScreenWidthAndHeight(context);
        lp.width = wh[0] - wh[0] / percentageW;
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * 获取当前window width,height
     *
     * @param context
     * @return
     */
    private static int[] initWithScreenWidthAndHeight(Context context) {
        int[] wh = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        wh[0] = dm.widthPixels;
        wh[1] = dm.heightPixels;
        return wh;
    }

    /**
     * Dialog 的基础方法，
     * 凡是要用的就在这写出来，然后直接用对话框调本来的方法就好了，不够自己加~hhh
     */

    //像这类设置对话框属性的方法，就返回值写自己，这样就可以一条链式设置了
    public BaseDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public void show() {
        initData();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public BaseDialog setdismissListeren(DialogInterface.OnDismissListener dismissListener) {
        dialog.setOnDismissListener(dismissListener);
        return this;
    }
}
