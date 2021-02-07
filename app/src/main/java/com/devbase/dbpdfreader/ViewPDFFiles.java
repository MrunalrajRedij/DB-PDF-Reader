package com.devbase.dbpdfreader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
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
import com.jakewharton.processphoenix.ProcessPhoenix;

import androidx.annotation.NonNull;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

import in.gauriinfotech.commons.Commons;

import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ViewPDFFiles extends AppCompatActivity {

    boolean perm = false;

    //private static final String TAG="" ;
    PDFView pdfView;
    int position = -1;

    Uri uri;
    public static Uri showuri;

    String fileName;

    boolean fromOutsidebool = false;

    boolean hView,sView =true;
    boolean vView,fView = false;
    boolean n_dView = false;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_p_d_f_files);
        Toolbar toolbar1 = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                perm = true;
            }else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }else{
            perm = true;
        }


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

        ActionBar actionBar = getSupportActionBar();

        pdfView = findViewById(R.id.pdfView);
        position = getIntent().getIntExtra("position",-1);

        uri = getIntent().getData();


        if(uri!=null){
            MainActivity.fromFilebool = false;
            createPDFActivity.fromCreatebool =false;
            pdfList.pdfFromlist = false;
            fromOutsidebool = true;
        }

        if(MainActivity.fromFilebool){
            showuri = MainActivity.uri1;
        }else if(fromOutsidebool){
            showuri = uri;
        }else if(createPDFActivity.fromCreatebool){
            showuri = Uri.fromFile(createPDFActivity.fromCreate);
        }else if(pdfList.pdfFromlist){
            showuri = Uri.fromFile(pdfList.fileList.get(position));
        }

        if(MainActivity.fromFilebool || fromOutsidebool){
            //file name
            Uri tempUri = showuri;
            Cursor cursor = getContentResolver().query(tempUri,null,null,null,null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            fileName = cursor.getString(nameIndex);
            actionBar.setTitle(fileName);
        }
        else if(createPDFActivity.fromCreatebool){
            actionBar.setTitle(createPDFActivity.fromCreate.getName());
        }else if(pdfList.pdfFromlist){
            actionBar.setTitle(pdfList.fileList.get(position).getName());
        }

        PDF1();

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

            try {
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("application/pdf");
                intentShare.putExtra(Intent.EXTRA_STREAM,showuri);

                startActivity(Intent.createChooser(intentShare, "Share file by :"));
            }
            catch (Exception e){
                Toast.makeText(this,"Go back and try again !",Toast.LENGTH_SHORT).show();
            }

        }
        else if(id == R.id.deleteFile) {
            if(perm) {
                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(ViewPDFFiles.this);
                alertDialog1.setMessage("Delete file?");
                alertDialog1.setCancelable(false);
                alertDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            String delPath = null;
                            delPath = PathUtil.getPath(ViewPDFFiles.this,showuri);
                            File file = new File(delPath);
                            boolean deleted = file.delete();
                            try {
                                if (deleted) {
                                    Toast.makeText(ViewPDFFiles.this, "PDF deleted", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            } catch (Exception e) {
                                Toast.makeText(ViewPDFFiles.this, "Try again or delete from file manager", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(ViewPDFFiles.this, "Delete file by opening file within the app!", Toast.LENGTH_LONG).show();

                            final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(ViewPDFFiles.this);
                            alertDialog1.setMessage("Open home page of the app?");
                            alertDialog1.setCancelable(false);
                            alertDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewPDFFiles.this,MainActivity.class));
                                }
                            });
                            alertDialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog1.create();
                            alertDialog1.show();


                        }





                    }
                });
                alertDialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog1.create();
                alertDialog1.show();
            }
            else {
                Toast.makeText(ViewPDFFiles.this,"Give permission for completing task",Toast.LENGTH_LONG).show();
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
                ad.setMessage("Night mode is only present in default settings");
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
                pdfView.fromUri(showuri)
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
            } else {
                n_dView = false;
                item.setTitle("Night mode");
                pdfView.fromUri(showuri)
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
        else if(id == R.id.print){
            if(perm) {

                try {
                    String printPath = null;
                    printPath = PathUtil.getPath(ViewPDFFiles.this,showuri);

                    PrintManager printManager = (PrintManager) ViewPDFFiles.this.getSystemService(Context.PRINT_SERVICE);
                    try {
                        PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(ViewPDFFiles.this, printPath);

                        printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());
                    } catch (Exception e) {
                        Toast.makeText(ViewPDFFiles.this, "Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(ViewPDFFiles.this, "Print file by opening file within the app!", Toast.LENGTH_LONG).show();

                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(ViewPDFFiles.this);
                    alertDialog1.setMessage("Open home page of the app?");
                    alertDialog1.setCancelable(false);
                    alertDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ViewPDFFiles.this,MainActivity.class));
                        }
                    });
                    alertDialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog1.create();
                    alertDialog1.show();


                }

            }
            else{
                Toast.makeText(ViewPDFFiles.this, "Give permission for completing task", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void PDF1() {
        if(hView && !fView && sView && !vView ) {
            pdfView.fromUri(showuri)
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
        else if(hView && fView && !sView && !vView) {
            pdfView.fromUri(showuri)
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
        else if(!hView && fView && !sView && vView) {
            pdfView.fromUri(showuri)
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
        else if(!hView && !fView && sView && vView) {

            pdfView.fromUri(showuri)
                    .enableSwipe(true)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .swipeHorizontal(false)
                    .pageSnap(true)
                    .autoSpacing(true)
                    .pageFling(true)
                    .enableAntialiasing(true)
                    .load();
        }else {
            pdfView.fromUri(showuri)
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ViewPDFFiles.this,"Permission granted",Toast.LENGTH_LONG).show();
                perm = true;
            }else {
                Toast.makeText(ViewPDFFiles.this,"Permission has not been granted",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        showuri = null;
        super.onBackPressed();
        uri = null;
        MainActivity.uri1 = null;
        MainActivity.fromFilebool = false;
        fromOutsidebool = false;
        createPDFActivity.fromCreatebool =false;
        createPDFActivity.fromCreate = null;
    }
}