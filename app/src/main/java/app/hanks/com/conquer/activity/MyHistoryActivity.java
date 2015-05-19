package app.hanks.com.conquer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.adapter.MyHistoryAdapter;
import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.config.Constants;
import app.hanks.com.conquer.util.AlertDialogUtils;
import app.hanks.com.conquer.util.AlertDialogUtils.OkCallBack;
import app.hanks.com.conquer.util.CollectionUtils;
import app.hanks.com.conquer.util.ZixiUtil;
import app.hanks.com.conquer.util.ZixiUtil.DeleteCardistener;

public class MyHistoryActivity extends BaseActivity {

    private ListView         lv_history;
    private MyHistoryAdapter adapter;
    private ArrayList<Card>  cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        lv_history = (ListView) findViewById(R.id.lv_history);
        cardList = new ArrayList<Card>();
        adapter = new MyHistoryAdapter(context, cardList);
        lv_history.setAdapter(adapter);
        lv_history.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialogUtils.show(context, "删除记录", "确认删除吗？", "确定", "取消", new OkCallBack() {
                    public void onOkClick(DialogInterface dialog, int which) {
                        ZixiUtil.deleteMyCard(context, cardList.get(position).getObjectId(), new DeleteCardistener() {
                            @Override
                            public void onSuccess() {
                                cardList.remove(position);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(int errorCord, String msg) {
                            }
                        });
                    }
                }, null);

                return false;
            }
        });
        getDate();
    }

    /**
     * 获取card
     */
    private void getDate() {
        ZixiUtil.getAllMyCard(context, currentUser.getObjectId(), new ZixiUtil.GetCardListener() {
            public void onSuccess(List<Card> list) {
                if (CollectionUtils.isNotNull(list)) {
                    cardList.clear();
                    cardList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            public void onError(int errorCord, String msg) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(Constants.ACTION_DESTORY_PLAYER));
        super.onDestroy();
    }

    @Override
    public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
        tv_title.setText("我的自习");
    }

    @Override
    public View getContentView() {
        return View.inflate(context, R.layout.activity_history, null);
    }
}
