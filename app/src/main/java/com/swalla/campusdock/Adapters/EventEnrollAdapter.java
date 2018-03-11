package com.swalla.campusdock.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import static com.swalla.campusdock.Utils.Config.Urls.URL_BASE_FILES;

/**
 * Created by meetesh on 13/01/18.
 */

public class EventEnrollAdapter extends RecyclerView.Adapter<EventEnrollAdapter.EventEnrollViewHolder> {

    private Context mContext;
    private List<Event> eventList;

    public class EventEnrollViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date;
        private ImageView banner;
        private TextView tags;

        public EventEnrollViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.card_title);
            date = view.findViewById(R.id.card_expiry);
            banner = view.findViewById(R.id.card_image);
            tags = view.findViewById(R.id.card_tags);
        }
    }

    public EventEnrollAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public EventEnrollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card_enroll, parent, false);
        return new EventEnrollViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventEnrollViewHolder holder, int position) {
        final Event currentEvent = eventList.get(position);
        holder.title.setText(currentEvent.getEventName());
        Date endDate = Utils.fromISO8601UTC(currentEvent.getEndDate());
        String finalDate = endDate.getDate()+" "+Utils.parseMonth(endDate.getMonth());
        holder.date.setText("Event Ends on : "+finalDate);
        holder.tags.setText(currentEvent.getTags());

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
                try {
                    Glide.with(mContext).asBitmap().load(URL_BASE_FILES + currentEvent.getUrl()).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.banner.setImageBitmap(resource);
                            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
                            if(!folder.exists()){
                                folder.mkdirs();
                            }
                            folder = new File(folder, currentEvent.getUrl());
                            if(folder.exists()){
                                folder.delete();
                            }
                            try {
                                FileOutputStream fos = new FileOutputStream(folder);
                                resource.compress(Bitmap.CompressFormat.JPEG, 40, fos);
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    holder.banner.setImageResource(R.drawable.test_poster);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
