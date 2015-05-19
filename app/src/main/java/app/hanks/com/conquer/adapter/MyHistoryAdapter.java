package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Card;
import app.hanks.com.conquer.util.AudioUtils;
import app.hanks.com.conquer.util.TimeUtil;
import app.hanks.com.conquer.view.CircularImageView;

public class MyHistoryAdapter extends MyBaseAdpter<Card> {

    private ImageLoader loader;
    private AudioUtils  aUtils;

    public MyHistoryAdapter(Context context, ArrayList<Card> list) {
        super(context, list);
        loader = ImageLoader.getInstance();
        aUtils = AudioUtils.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        }
        Card card = list.get(position);
        for (Card c : list)
            L.d(c.toString());
        CircularImageView iv_photo = ViewHolder.get(convertView, R.id.iv_photo);
        TextView tv_from = ViewHolder.get(convertView, R.id.tv_from);
        TextView tv_type = ViewHolder.get(convertView, R.id.tv_type);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_zixiname = ViewHolder.get(convertView, R.id.tv_zixiname);
        TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
        View ll_audio = ViewHolder.get(convertView, R.id.ll_audio);
        ImageView iv = ViewHolder.get(convertView, R.id.iv);
        View loading = ViewHolder.get(convertView, R.id.loading);
        tv_from.setText(card.getFnick());
        if (card.getType() == 0) {
            tv_type.setText("自习提醒");
            tv_type.setTextColor(context.getResources().getColor(R.color.blue_normal));
        } else {
            tv_type.setText("自习勾搭");
            tv_type.setTextColor(context.getResources().getColor(R.color.red_button));
        }
        tv_time
                .setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(card.getCreatedAt(), TimeUtil.FORMAT_DATE_TIME_SECOND2)));
        tv_zixiname.setText(card.getZixiName());
        tv_content.setText(card.getContent());
        loading.setVisibility(View.GONE);
        ll_audio.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(card.getAudioUrl())) {
            ll_audio.setVisibility(View.VISIBLE);
            initAudio(ll_audio, card.getAudioUrl());
        }
        if (!TextUtils.isEmpty(card.getImgUrl())) {
            iv.setVisibility(View.VISIBLE);
            initAlbum(iv, card.getImgUrl());
        }

        loader.displayImage(card.getFavatar(), iv_photo);
        return convertView;
    }

    /**
     * 图片的
     *
     * @param string
     */
    private void initAlbum(ImageView iv, String string) {
        loader.displayImage(string, iv);
    }

    /**
     * 播放声音的
     *
     */
    private void initAudio(final View view, final String audioUrl) {
        final ImageButton ib_play = (ImageButton) view.findViewById(R.id.ib_play);
        // 删除隐藏该布局
        view.findViewById(R.id.iv_del).setVisibility(View.GONE);
        // 播放按钮
        ib_play.setImageResource(R.drawable.play_audio);
        ib_play.setTag("play");
        ib_play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aUtils.play(context, view, audioUrl);
            }
        });
    }

}
