package com.swalla.campusdock.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.swalla.campusdock.Adapters.BulletinFileAdapter;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Utils.Utils;
import com.swalla.campusdock.listeners.RecyclerItemClickListener;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.DownloadFileFromURL;
import com.swalla.campusdock.listeners.OnFileDownloadCompleteListener;

import java.util.Date;

import static com.swalla.campusdock.Utils.Config.Types.TYPE_BULLETIN;


public class BulletinActivity extends AppCompatActivity {
    private TextView cardTitle, cardDescription, chipText, cardDate, attachmentText, noFileText;
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
        final Bulletin bulletin = (Bulletin) getIntent().getSerializableExtra(TYPE_BULLETIN);

        cardTitle.setText(bulletin.getBulletinName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cardDescription.setText(Html.fromHtml(bulletin.getBulletinDescription(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            cardDescription.setText(Html.fromHtml(bulletin.getBulletinDescription()));
        }
        cardDescription.setMovementMethod(new ScrollingMovementMethod());
        chipText.setText(bulletin.getCreated_by());
        Date date = Utils.fromISO8601UTC(bulletin.getCreated_on());
        String finalDate = date.getDate()+ " "+Utils.parseMonth(date.getMonth())+" "+ (1900 + date.getYear());
        cardDate.setText(finalDate);

        attachmentText = findViewById(R.id.attachmentText);
        noFileText = findViewById(R.id.noFileText);

        if(bulletin.getFiles().length()==0){
            fileList.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
            noFileText.setVisibility(View.VISIBLE);
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
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
                        String mimeType = myMime.getMimeTypeFromExtension(fileExt(DownloadFileFromURL.fileUrl(files[position]).getAbsolutePath()).substring(1));
                        newIntent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".my.package.name.provider", DownloadFileFromURL.fileUrl(files[position])),mimeType);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(newIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        OnFileDownloadCompleteListener listener = new OnFileDownloadCompleteListener() {
                            @Override
                            public void OnFileDownloadComplete() {
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

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    @Override
    public void onBackPressed() {
        fileList.setVisibility(View.GONE);
        attachmentText.setVisibility(View.GONE);
        noFileText.setVisibility(View.GONE);
        super.onBackPressed();
    }
}