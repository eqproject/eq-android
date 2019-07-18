package org.eq.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.eq.android.R;


/**
 * 单选框
 *
 * @author sugy
 */
public class SelectDialog {

    private Context context;

    private String[] data;

    private Dialog selectDialog;

    public SelectDialog(Context context, String[] data) {
        this(context, data, false);
    }

    public SelectDialog(Context context, String[] data, boolean isNeedSelect) {
        super();
        this.context = context;
        this.data = data;
    }



    /**
     * 显示
     *
     * @param title
     * @return
     */
    public  ListView show(CharSequence title) {

        selectDialog = new Dialog(context, R.style.DialogStyle);
        View view = View.inflate(context, R.layout.select_dialog, null);
        ListView selectLv = (ListView) view.findViewById(R.id.select_lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.item_select_dialog, R.id.item_text, data);
        selectLv.setAdapter(adapter);
        selectDialog.setContentView(view);
        selectDialog.setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(title))
            selectDialog.setTitle(title);
        if (!selectDialog.isShowing()) {
            selectDialog.show();
        }
        return selectLv;
    }

    /**
     * 选中
     *
     * @param listView
     * @param selectIndex
     */
    public void setSelect(final ListView listView, final int selectIndex) {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childView = listView.getChildAt(selectIndex);
                if (childView != null)
                    childView.setSelected(true);
            }
        }, 100);
    }

    /**
     * 取消显示
     */
    public void dismiss() {
        if (selectDialog != null)
            selectDialog.dismiss();
    }

    /**
     * 消失监听器
     *
     * @param dismissListener
     */
    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        if (selectDialog != null) {
            selectDialog.setOnDismissListener(dismissListener);
        }
    }

}
