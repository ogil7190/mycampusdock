package com.swalla.campusdock.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.swalla.campusdock.Adapters.BulletinFileAdapter;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Classes.RecyclerItemClickListener;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Utils.DownloadFileFromURL;
import com.swalla.campusdock.Utils.OnFileDownloadCompleteListener;

import es.dmoral.toasty.Toasty;


public class BulletinActivity extends AppCompatActivity {
    private TextView cardTitle, cardDescription, chipText, cardDate, attachmentText;
    private RecyclerView fileList;
    private View indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);
        cardTitle = findViewById(R.id.card_title);
        cardDescription = findViewById(R.id.card_desc);
        chipText = findViewById(R.id.chipText);
        fileList = findViewById(R.id.recycler_view);
        cardDate = findViewById(R.id.card_date);
        indicator = findViewById(R.id.attachmentIndicator);
        final Bulletin bulletin = (Bulletin) getIntent().getSerializableExtra(Config.TYPE_BULLETIN);

        cardTitle.setText(bulletin.getBulletinName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cardDescription.setText(Html.fromHtml(bulletin.getBulletinDescription(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            cardDescription.setText(Html.fromHtml(bulletin.getBulletinDescription()));
        }
        cardDescription.setMovementMethod(new ScrollingMovementMethod());
        chipText.setText(bulletin.getCreated_by());
        cardDate.setText(bulletin.getCreated_on());

        attachmentText = findViewById(R.id.attachmentText);
        if(bulletin.getFiles().length()==0){
            fileList.setVisibility(View.GONE);
            attachmentText.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
        } else {
            final String[] files = bulletin.getFiles().split(",");
            BulletinFileAdapter adapter = new BulletinFileAdapter(getApplicationContext(), files);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            fileList.setLayoutManager(mLayoutManager);
            fileList.setItemAnimator(new DefaultItemAnimator());
            fileList.setAdapter(adapter);
            fileList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), fileList, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(final View view, final int position) {
                    ProgressBar progressBar = view.findViewById(R.id.progressBar);
                    if (DownloadFileFromURL.fileExists(files[position])) {
                        Toasty.normal(getApplicationContext(), "We have this File!", Toast.LENGTH_SHORT).show();
                    } else {
                        OnFileDownloadCompleteListener listener = new OnFileDownloadCompleteListener() {
                            @Override
                            public void OnFileDownloadComplete() {
                                Log.d("App", "File Download Listener");
                                if (DownloadFileFromURL.fileExists(files[position])) {
                                    ImageView imageView = view.findViewById(R.id.imageDownload);
                                    imageView.setImageResource(R.drawable.ic_cloud_done_black_24dp);
                                }
                            }
                        };
                        DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(progressBar, listener);
                        downloadFileFromURL.execute(files[position]);
                    }
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }

    @Override
    public void onBackPressed() {
        fileList.setVisibility(View.GONE);
        attachmentText.setVisibility(View.GONE);

        super.onBackPressed();
    }
}