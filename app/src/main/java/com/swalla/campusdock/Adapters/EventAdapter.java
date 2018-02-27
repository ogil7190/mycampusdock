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

import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.R;

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
        private TextView title, date, organizer, category;
        private ImageView banner;

        public EventViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.card_title);
            date = view.findViewById(R.id.card_date);
            organizer=  view.findViewById(R.id.card_organizer);
            banner = view.findViewById(R.id.card_image);
            category = view.findViewById(R.id.card_category);
        }
    }

    public EventAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        final Event currentEvent = eventList.get(position);
        holder.title.setText(currentEvent.getEventName());
        holder.date.setText(currentEvent.getDate());
        holder.organizer.setText(currentEvent.getOrganizer());
        holder.category.setText(currentEvent.getCategory());
        if(currentEvent.getUrl() == null) {
            holder.banner.setImageResource(currentEvent.getBanner());
            Log.d("App", "No URL Found!");
        }
        else {
            try {
                File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
                File f = new File(folder, currentEvent.getUrl());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                holder.banner.setImageBitmap(b);
            } catch (Exception e) {
                e.printStackTrace();
                holder.banner.setImageResource(currentEvent.getBanner());
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
