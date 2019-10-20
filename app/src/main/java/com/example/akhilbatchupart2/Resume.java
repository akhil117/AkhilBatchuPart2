package com.example.akhilbatchupart2;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Resume extends AppCompatActivity {


    TextView linkedin, github, phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        getSupportActionBar().setTitle("Resume");
        linkedin = (TextView) findViewById(R.id.linkedinUrl);
        phoneText = (TextView) findViewById(R.id.phoneText);
        github = (TextView) findViewById(R.id.githubUrl);
        linkedin.setPaintFlags(linkedin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        github.setPaintFlags(github.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        phoneText.setPaintFlags(phoneText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void redirectToLinkedin(View view) {
        Uri uri = Uri.parse("http://www.linkedin.com/in/akhilbatchu");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void redirectToGithub(View view) {
        Uri uri = Uri.parse("http://github.com/akhil117");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void makeCall(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(phoneText.getText() + "")));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);

    }

    public void getLocation(View view) {
        Uri addressUri = Uri.parse("geo:0,0?q=" + "Amrita School of Engineering, Amritapuri");
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't open the location!");
        }
    }
}
