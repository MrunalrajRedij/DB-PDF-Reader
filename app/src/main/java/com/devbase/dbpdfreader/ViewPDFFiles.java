package com.devbase.dbpdfreader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.customview.widget.Openable;
import androidx.documentfile.provider.DocumentFile;

import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Logger;

import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ViewPDFFiles extends AppCompatActivity {


    private static final String TAG="" ;
    PDFView pdfView;
    int position = -1;

    Uri uri;

    String filePath0,filPath1,filePath2,filePath3;

    boolean fromOutsidebool = false;

    boolean hView,sView =true;
    boolean vView,fView = false;
    boolean n_dView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_p_d_f_files);
        Toolbar toolbar1 = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);

        //Banner ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //


        pdfView = findViewById(R.id.pdfView);
        position = getIntent().getIntExtra("position",-1);

        uri = getIntent().getData();
        if(uri!=null){
            MainActivity.fromFilebool = false;
            fromOutsidebool = true;
        }


        ActionBar actionBar = getSupportActionBar();


        if(MainActivity.fromFilebool){
            //file name
            Uri tempUri = MainActivity.uri1;
            Cursor cursor = getContentResolver().query(tempUri,null,null,null,null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            filePath0 = cursor.getString(nameIndex);
            actionBar.setTitle(filePath0);

            displaPDFfromFile();
        }
        else if(fromOutsidebool){

            //file name
            Uri tempUri = uri;
            Cursor cursor = getContentResolver().query(tempUri, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            filePath2 = cursor.getString(nameIndex);
            actionBar.setTitle(filePath2);

            displayPDFfromOutside();
        }
        else if(createPDFActivity.fromCreatebool){
            actionBar.setTitle(createPDFActivity.fromCreate.getName());
            filePath3 = createPDFActivity.fromCreate.getPath();

            diaplayPDFfromCreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pdf_view, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shareFile) {
            if(MainActivity.fromFilebool){
                try {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("application/pdf");
                    intentShare.putExtra(Intent.EXTRA_STREAM,MainActivity.uri1);

                    startActivity(Intent.createChooser(intentShare, "Share file by :"));
                }
                catch (Exception e){
                    Toast.makeText(this,"Go back and try again !",Toast.LENGTH_SHORT).show();
                }
            }else if(fromOutsidebool){
                try {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("application/pdf");
                    intentShare.putExtra(Intent.EXTRA_STREAM, uri);

                    startActivity(Intent.createChooser(intentShare, "Share file by :"));
                }
                catch (Exception e){
                    Toast.makeText(this,"Go back and try again !",Toast.LENGTH_LONG).show();
                }
            }
            else if(createPDFActivity.fromCreatebool){
                try {

                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("application/pdf");
                    intentShare.putExtra(Intent.EXTRA_STREAM,Uri.parse(filePath3));
                    startActivity(Intent.createChooser(intentShare,"Share file by :"));

                }catch (Exception e){
                    Toast.makeText(this,"Go back and try again !",Toast.LENGTH_LONG).show();
                }
            }

        }
        else if (id == R.id.verticalView){
            vView = true;
            hView = false;
            PDF1();
        }
        else if (id == R.id.horizontalView){
            hView=true;
            vView=false;
            PDF1();
        }
        else if(id == R.id.night_dayView){

            if(!n_dView){


                AlertDialog.Builder ad = new AlertDialog.Builder(ViewPDFFiles.this);
                ad.setTitle("Night mode is unstable and\nonly present in default settings");
                ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.create();
                ad.show();


                n_dView = true;
                item.setTitle("Normal mode");
                if (MainActivity.fromFilebool) {
                    pdfView.fromUri(MainActivity.uri1)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .nightMode(true)
                            .load();

                }else if (fromOutsidebool) {
                    pdfView.fromUri(uri)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .nightMode(true)
                            .load();
                } else if (createPDFActivity.fromCreatebool) {
                    pdfView.fromFile(createPDFActivity.fromCreate)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .nightMode(true)
                            .load();
                }
            }
            else {
                n_dView = false;
                item.setTitle("Night mode");
                if (MainActivity.fromFilebool) {
                    pdfView.fromUri(MainActivity.uri1)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .load();

                }else if (fromOutsidebool) {
                    pdfView.fromUri(uri)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .load();
                }
                else if (createPDFActivity.fromCreatebool) {
                    pdfView.fromFile(createPDFActivity.fromCreate)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .load();
                }
            }
        }
        else if (id == R.id.snapView){
            sView=true;
            fView=false;
            PDF1();
        }
        else if (id == R.id.freeView){
            fView=true;
            sView=false;
            PDF1();
        }


        return super.onOptionsItemSelected(item);
    }

    private void displaPDFfromFile() {
        pdfView.fromUri(MainActivity.uri1)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .enableAntialiasing(true)
                .load();
    }


    private void displayPDFfromOutside() {
        pdfView.fromUri(uri)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .enableAntialiasing(true)
                .load();
    }

    private void diaplayPDFfromCreate(){
        pdfView.fromFile(createPDFActivity.fromCreate)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .enableAntialiasing(true)
                .load();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void PDF1() {
        if(hView && !fView && sView && !vView ) {
                if (MainActivity.fromFilebool) {
                    pdfView.fromUri(MainActivity.uri1)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .load();

                }else if (fromOutsidebool) {
                    pdfView.fromUri(uri)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .load();
                }else if (createPDFActivity.fromCreatebool) {
                    pdfView.fromFile(createPDFActivity.fromCreate)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(true)
                            .autoSpacing(true)
                            .pageFling(true)
                            .enableAntialiasing(true)
                            .load();
                }
        }
        else if(hView && fView && !sView && !vView) {
                if (MainActivity.fromFilebool) {
                    pdfView.fromUri(MainActivity.uri1)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(false)
                            .autoSpacing(false)
                            .pageFling(false)
                            .enableAntialiasing(true)
                            .load();

                }else if (fromOutsidebool) {
                    pdfView.fromUri(uri)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(false)
                            .autoSpacing(false)
                            .pageFling(false)
                            .enableAntialiasing(true)
                            .load();
                }else if (createPDFActivity.fromCreatebool) {
                    pdfView.fromFile(createPDFActivity.fromCreate)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(true)
                            .pageSnap(false)
                            .autoSpacing(false)
                            .pageFling(false)
                            .enableAntialiasing(true)
                            .load();
                }
        }
        else if(!hView && fView && !sView && vView) {
                if (MainActivity.fromFilebool) {
                    pdfView.fromUri(MainActivity.uri1)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(false)
                            .pageSnap(false)
                            .autoSpacing(false)
                            .pageFling(false)
                            .enableAntialiasing(true)
                            .load();

                }else if (fromOutsidebool) {
                    pdfView.fromUri(uri)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(false)
                            .pageSnap(false)
                            .autoSpacing(false)
                            .pageFling(false)
                            .enableAntialiasing(true)
                            .load();
                }else if (createPDFActivity.fromCreatebool) {
                    pdfView.fromFile(createPDFActivity.fromCreate)
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .swipeHorizontal(false)
                            .pageSnap(false)
                            .autoSpacing(false)
                            .pageFling(false)
                            .enableAntialiasing(true)
                            .load();
                }
        }
        else if(!hView && !fView && sView && vView) {
            if (MainActivity.fromFilebool) {
                pdfView.fromUri(MainActivity.uri1)
                        .enableSwipe(true)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(this))
                        .swipeHorizontal(false)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .pageFling(true)
                        .enableAntialiasing(true)

                        .load();

            }else if (fromOutsidebool) {
                pdfView.fromUri(uri)
                        .enableSwipe(true)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(this))
                        .swipeHorizontal(false)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .pageFling(true)
                        .enableAntialiasing(true)
                        .load();
            }else if (createPDFActivity.fromCreatebool) {
                pdfView.fromFile(createPDFActivity.fromCreate)
                        .enableSwipe(true)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(this))
                        .swipeHorizontal(false)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .pageFling(true)
                        .enableAntialiasing(true)
                        .load();
            }
        }
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        uri = null;
        MainActivity.uri1 = null;
        MainActivity.fromFilebool = false;
        fromOutsidebool = false;
        createPDFActivity.fromCreatebool =false;
        createPDFActivity.fromCreate = null;

    }
}