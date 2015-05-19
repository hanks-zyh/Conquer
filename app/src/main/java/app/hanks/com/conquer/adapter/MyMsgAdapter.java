package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.MyMsg;

/**
 * 留言板的适配器
 *
 * @author zyh
 */
public class MyMsgAdapter<E> extends MyBaseAdpter<E> {

    public MyMsgAdapter(Context context, ArrayList<E> list) {
        super(context, list);
        loder = ImageLoader.getInstance();
    }

    private ImageLoader loder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_mymsg, null);
        }
        ImageView iv_photo = ViewHolder.get(convertView, R.id.iv_photo);
        TextView tv_cotent = ViewHolder.get(convertView, R.id.tv_cotent);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_nickname = ViewHolder.get(convertView, R.id.tv_nickname);
        TextView tv_reply = ViewHolder.get(convertView, R.id.tv_reply);

        MyMsg msg = (MyMsg) list.get(position);
        loder.displayImage(msg.getUser().getAvatar(), iv_photo);
        tv_nickname.setText(msg.getUser().getNick());
        tv_cotent.setText(msg.getContent());

        return convertView;
    }

}
