package com.devbase.dbpdfreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    static Uri uri1;

    static boolean fromFilebool = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sp = getSharedPreferences("JP",MODE_PRIVATE);
        boolean bln = sp.getBoolean("ONCE",true);

        if(bln){

            AlertDialog.Builder mainAlert = new AlertDialog.Builder(MainActivity.this);
            mainAlert.setTitle("WELCOME");
            mainAlert.setMessage("And sorry for enabling ads.\n" +
                    "As a student it fill up my coffee mug.\n" +
                    "I will remove them with my graduation.\n"+
                    "Pinky Primise!");
            mainAlert.setCancelable(false);
            mainAlert.setPositiveButton("I get it", (dialog, which) -> dialog.dismiss());
            mainAlert.show();

            SharedPreferences.Editor ediotr = sp.edit();
            ediotr.putBoolean("ONCE",false);
            ediotr.apply();

        }



        ExtendedFloatingActionButton newBtn = findViewById(R.id.newBtn);
        newBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,createPDFActivity.class)));


        ExtendedFloatingActionButton openParticularPDFBtn =findViewById(R.id.open_pdf);
        openParticularPDFBtn.setOnClickListener(v -> {
             Intent pdfFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
             pdfFileIntent.setType("application/pdf");
             startActivityForResult(pdfFileIntent,1);
        });

        ExtendedFloatingActionButton AboutBtn = findViewById(R.id.AboutBtn);
        AboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,aboutActivity.class));

            }
        });

        ExtendedFloatingActionButton ppBtn = findViewById(R.id.ppBtn);
        ppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://devbaseflashlight.blogspot.com/2021/01/db-pdf-reader-policy.html";
                Uri uri = Uri.parse(url);
                Intent launchPrivacyPolicy = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(launchPrivacyPolicy);
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
        } else if(id == R.id.contact){
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("mailto:" + "devbase123@gmail.com"));
            startActivity(intent);
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