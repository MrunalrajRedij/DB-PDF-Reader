package com.devbase.dbpdfreader;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class createPDFActivity extends AppCompatActivity {

    EditText title,body,name;
    Button export;

    static File fromCreate;
    static boolean fromCreatebool = false;

    private AdView iadView;
    InterstitialAd mInterstitialAd;
    private  InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_p_d_f);

        //Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create PDF");

        //mainCreate alert box
        AlertDialog.Builder mainCreate = new AlertDialog.Builder(createPDFActivity.this);
        mainCreate.setTitle("PDF creation is still in beta.\nMore options will be added soon ! ");
        mainCreate.setCancelable(false);
        mainCreate.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mainCreate.create();
        mainCreate.show();

        name= findViewById(R.id.pdfName);
        title = findViewById(R.id.pdfTitle);
        body = findViewById(R.id.pdfBody);
        export = findViewById(R.id.createPDF);


        //Interstitial ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(createPDFActivity.this);
                alertDialog.setTitle("Create PDF ?");
                alertDialog.setCancelable(false);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nameT = name.getText().toString();
                        String bodyT = body.getText().toString();
                        String titleT = title.getText().toString();
                        String path = getExternalFilesDir(null).toString()+"/"+nameT+".pdf";
                        File file = new File(path);
                        if(!file.exists()){
                            try {
                                file.createNewFile();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        Document document = new Document(PageSize.A4);
                        try {
                            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        document.open();
                        try {
                            document.add(new Paragraph(titleT,new Font(Font.FontFamily.UNDEFINED,50,Font.BOLD)));
                            document.add(new Paragraph("\n"));
                            document.add(new Paragraph(bodyT,new Font(Font.FontFamily.UNDEFINED,30,Font.NORMAL)));

                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"PDF created successfully",Toast.LENGTH_LONG).show();
                        document.close();

                        AdRequest adRequest = new AdRequest.Builder().build();
                        interstitial = new InterstitialAd(createPDFActivity.this);
                        interstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
                        interstitial.loadAd(adRequest);
                        interstitial.setAdListener(new AdListener(){
                            public void onAdLoaded(){
                                displayInterstitial();
                            }
                        });

                        
                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(createPDFActivity.this);
                        alertDialog2.setTitle("Open recently created PDF ?");
                        alertDialog2.setCancelable(false);
                        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (file.exists()) {
                                    fromCreatebool = true;
                                    fromCreate = new File(path);
                                    onBackPressed();
                                    startActivity(new Intent(createPDFActivity.this,ViewPDFFiles.class));
                                }
                            }
                        });
                        alertDialog2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });

                        AlertDialog alertDialog3 = alertDialog2.create();
                        alertDialog3.show();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();
            }
        });
    }

    private void displayInterstitial(){
        if(interstitial.isLoaded()){
            interstitial.show();
            interstitial.setImmersiveMode(true);
        }
    }
}