package com.swalla.campusdock.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Utils.NotiUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Created by meetesh on 13/01/18.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context mContext;
    private List<Event> eventList;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date;
        private ImageView banner;
        private TextView chipText;

        public EventViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.card_title);
            date = view.findViewById(R.id.card_date);
            banner = view.findViewById(R.id.card_image);
            chipText = view.findViewById(R.id.chipText);
        }
    }

    public EventAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        final Event currentEvent = eventList.get(position);
        holder.title.setText(currentEvent.getEventName());
        holder.date.setText(currentEvent.getDate()+" - "+currentEvent.getEndDate());
        holder.chipText.setText(currentEvent.getCreated_by());

        if(currentEvent.getUrl() == null) {
            holder.banner.setImageResource(R.drawable.test_poster);
        }
        else {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
            File f = new File(folder, currentEvent.getUrl());
            if(f.exists())
                Glide.with(mContext).load(f).into(holder.banner);
            else{
                holder.banner.setImageResource(R.drawable.test_poster);
                NotiUtil.getBitmapFromURL(currentEvent.getUrl());
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
