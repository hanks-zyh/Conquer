package app.hanks.com.conquer.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;


/**
 * 弹出对话框
 * 
 * @author zyh
 */
public class AlertDialogUtils {

    /**
     * 弹出确认，取消对话框
     * 
     * @param context
     * @param title
     * @param msg
     * @param ok
     * @param cancel
     * @param okcallback
     * @param cancelCallBack
     */
    public static void show(Context context, String title, String msg, String ok, String cancel,
            final OkCallBack okcallback, final CancelCallBack cancelCallBack) {
        Builder builder = new Builder(context);
        if (!TextUtils.isEmpty(title))
            builder.setTitle(title);
        if (!TextUtils.isEmpty(msg))
            builder.setMessage(msg);
        if (!TextUtils.isEmpty(ok)) {
            builder.setPositiveButton(ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    okcallback.onOkClick(dialog, which);
                }
            });
        }
        if (!TextUtils.isEmpty(cancel)) {
            if (cancelCallBack != null) {
                builder.setNegativeButton(cancel, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelCallBack.onCancelClick(dialog, which);
                    }
                });
            } else {
                builder.setNegativeButton(cancel, null);
            }
        }
        builder.show();
    }

    /**
     * 单选对话框
     * 
     * @param context
     * @param s
     * @param defaltItem
     * @param okcallback
     */
    public static void showChiceGender(Context context, String[] s, int defaltItem,
            final OkCallBack okcallback) {
        new Builder(context).setSingleChoiceItems(s, defaltItem,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        okcallback.onOkClick(dialog, which);
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 多选对话框
     * 
     * @param context
     * @param s
     * @param okcallback
     */
    public static void showSelectFriends(Context context, final String[] s, final MultCallBack okcallback) {
        new Builder(context).setMultiChoiceItems(s, new boolean[] {},
                new OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        List<Integer> checked = new ArrayList<Integer>();
                        for (int i = 0; i < s.length; i++) {
                            if (i == which && isChecked) {
                                // 表示选中了
                                checked.add(i);
                            }
                        }
                        okcallback.onOkClick(checked);
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showEditDialog(Context context, String title, String ok, String cancel,
            final EtOkCallBack okcallback) {
        final AlertDialog dialog = new Builder(context, AlertDialog.THEME_HOLO_LIGHT).create();
        View view = View.inflate(context, R.layout.dialog_edit, null);
        final EditText et = (EditText) view.findViewById(R.id.et);
        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_ok.setText(ok);
        bt_cancel.setText(cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et.getText().toString().trim();
                if (!TextUtils.isEmpty(s)) {
                    okcallback.onOkClick(s);
                }
                dialog.dismiss();
            }
        });
        // 设置显示动画
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    public interface OkCallBack {
        public void onOkClick(DialogInterface dialog, int which);
    }

    public interface MultCallBack {
        public void onOkClick(List<Integer> checked);
    }

    public interface EtOkCallBack {
        public void onOkClick(String s);
    }

    public interface CancelCallBack {
        public void onCancelClick(DialogInterface dialog, int which);
    }
}
