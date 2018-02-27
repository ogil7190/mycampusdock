package com.swalla.campusdock.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;


public class BulletinActivity extends AppCompatActivity {
    private TextView cardTitle, cardDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_event);
        cardTitle = findViewById(R.id.card_title);
        cardDescription = findViewById(R.id.card_desc);
        final Bulletin bulletin = (Bulletin) getIntent().getSerializableExtra(Config.TYPE_BULLETIN);
        cardTitle.setText(bulletin.getBulletinName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cardDescription.setText(Html.fromHtml(bulletin.getBulletinDescription(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            cardDescription.setText(Html.fromHtml(bulletin.getBulletinDescription()));
        }
    }
}


