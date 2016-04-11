package com.ian.android.templateproject.common.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ian.android.templateproject.R;
import com.ian.android.templateproject.common.imageload.URLRoundImageView;
import com.ian.android.templateproject.entity.ForceGroupInfo;

import java.util.List;

/**
 * ********
 *
 * @author Ian
 * @date 2016-12-14 11:31
 * @describ 加入圈子 弹窗的适配器
 */
public class ForceDialogAdapter extends BaseAdapter {

    private List<ForceGroupInfo> datas;
    private Context context;

    public ForceDialogAdapter(Context context, List<ForceGroupInfo> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_force_dialog, null, false);
            holder.item_icon = (URLRoundImageView) convertView.findViewById(R.id.item_icon);
            holder.item_state = (ImageView) convertView.findViewById(R.id.item_state);
            holder.item_text = (TextView) convertView.findViewById(R.id.item_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ForceGroupInfo info = datas.get(position);

        holder.item_icon.loadURL(info.url, info.resId);
        holder.item_text.setText(info.name);

        if (info.flag) {
            holder.item_state.setVisibility(View.VISIBLE);
        } else {
            holder.item_state.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setDatas(List<ForceGroupInfo> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    private final class ViewHolder {
        public URLRoundImageView item_icon;
        public ImageView item_state;
        public TextView item_text;

    }
}
