package com.swalla.campusdock.Adapters;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by minion on 27/02/2018 AD.
 */

public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.ClassViewHolder>{
    private Context mContext;
    private List<Bulletin> bulletinList;

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView chipText;
        private TextView date;
        private View indicator;

        public ClassViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.card_title);
            chipText = view.findViewById(R.id.chipText);
            date = view.findViewById(R.id.card_date);
            indicator = view.findViewById(R.id.attachmentIndicator);
        }
    }

    public BulletinAdapter(Context mContext, List<Bulletin> bulletinList) {
        this.mContext = mContext;
        this.bulletinList = bulletinList;
    }

    @Override
    public BulletinAdapter.ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card_bulletin, parent, false);
        return new BulletinAdapter.ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BulletinAdapter.ClassViewHolder holder, int position) {
        final Bulletin currentBulletin = bulletinList.get(position);
        holder.title.setText(currentBulletin.getBulletinName());
        holder.chipText.setText(currentBulletin.getCreated_by());
        holder.date.setText(currentBulletin.getCreated_on());
        if(currentBulletin.getFiles().length()==0){
            holder.indicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bulletinList.size();
    }
}
