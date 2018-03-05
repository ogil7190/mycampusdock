package com.swalla.campusdock.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.DownloadFileFromURL;

import java.util.List;

/**
 * Created by minion on 27/02/2018 AD.
 */

public class BulletinFileAdapter extends RecyclerView.Adapter<BulletinFileAdapter.ClassViewHolder>{
    private Context mContext;
    private String[] fileList;

    public class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private ImageView imageDownload;

        public ClassViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.fileName);
            imageDownload = view.findViewById(R.id.imageDownload);
        }
    }

    public BulletinFileAdapter(Context mContext, String[] fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
    }

    @Override
    public BulletinFileAdapter.ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_file_list_view, parent, false);
        return new BulletinFileAdapter.ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BulletinFileAdapter.ClassViewHolder holder, int position) {
        String url = fileList[position];
        if(DownloadFileFromURL.fileExists(url)){
            holder.imageDownload.setImageResource(R.drawable.ic_cloud_done_black_24dp);
        }
        String[] names = fileList[position].split("-");
        holder.fileName.setText(names[names.length-1]); //last name
    }

    @Override
    public int getItemCount() {
        return fileList.length;
    }
}
