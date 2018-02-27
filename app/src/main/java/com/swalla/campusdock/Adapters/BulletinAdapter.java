package com.swalla.campusdock.Adapters;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by minion on 27/02/2018 AD.
 */

public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.ClassViewHolder>{
    private Context mContext;
    private List<Bulletin> bulletinList;

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date, organizer, category;
        private ImageView banner;

        public ClassViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.card_title);
            date = view.findViewById(R.id.card_date);
            organizer=  view.findViewById(R.id.card_organizer);
            banner = view.findViewById(R.id.card_image);
            category = view.findViewById(R.id.card_category);
        }
    }

    public BulletinAdapter(Context mContext, List<Bulletin> bulletinList) {
        this.mContext = mContext;
        this.bulletinList = bulletinList;
    }

    @Override
    public BulletinAdapter.ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card_bulletin, parent, false);
        return new BulletinAdapter.ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BulletinAdapter.ClassViewHolder holder, int position) {
        final Bulletin currentBulletin = bulletinList.get(position);
        holder.title.setText(currentBulletin.getBulletinName());

    }

    @Override
    public int getItemCount() {
        return bulletinList.size();
    }
}
