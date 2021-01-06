package com.devbase.dbpdfreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    static Uri uri1;

    static boolean fromFilebool = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);







        FloatingActionButton newBtn =(FloatingActionButton) findViewById(R.id.newBtn);


        ExtendedFloatingActionButton openParticularPDFBtn =(ExtendedFloatingActionButton) findViewById(R.id.open_pdf);
        openParticularPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent pdfFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                 pdfFileIntent.setType("application/pdf");
                 startActivityForResult(pdfFileIntent,1);
            }
        });

        ExtendedFloatingActionButton openPDFListBtn = (ExtendedFloatingActionButton) findViewById(R.id.open_pdf_list);
        openPDFListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,pdfListView.class));
            }
        });




    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            try {
                Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
                shareAppIntent.setType("text/plane");
                shareAppIntent.putExtra(Intent.EXTRA_SUBJECT,"Download DB PDF Reader here:");
                String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID+"\n\n";
                shareAppIntent.putExtra(Intent.EXTRA_TEXT,shareMessage);
                startActivity(Intent.createChooser(shareAppIntent,"Share app"));
            }
            catch (Exception e){
                Toast.makeText(MainActivity.this,"Try again!",Toast.LENGTH_SHORT).show();
            }
        }
        else if(id == R.id.about){

        }
        else if(id == R.id.contact){

        }
        else if(id == R.id.privacyPolicy){

        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            if(data !=null) {
                uri1 = data.getData();
                fromFilebool = true;
                startActivity(new Intent(MainActivity.this, ViewPDFFiles.class));
            }
        }

    }
}