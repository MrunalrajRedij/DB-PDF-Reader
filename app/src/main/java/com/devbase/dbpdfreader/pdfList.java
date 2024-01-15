package com.devbase.dbpdfreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.File;
import java.util.ArrayList;

public class pdfList extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<File>();
    PDFAdapter obj_adapter;
    boolean perm;
    File dir;

    public static boolean pdfFromlist = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);

        lv_pdf = findViewById(R.id.listViewPDF);


        dir = new File(Environment.getExternalStorageDirectory().toString());

        permission_fn();



        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pdfFromlist = true;
                Intent intent = new Intent(pdfList.this,ViewPDFFiles.class);
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            Intent intentlist = getIntent();
            ProcessPhoenix.triggerRebirth(pdfList.this,intentlist);
        }

        return super.onOptionsItemSelected(item);
    }


    void permission_fn(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                perm = true;
                getfile(dir);
                obj_adapter = new PDFAdapter(getApplicationContext(),fileList);
                lv_pdf.setAdapter(obj_adapter);
            }else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }else{
            perm = true;
            getfile(dir);
            obj_adapter = new PDFAdapter(getApplicationContext(),fileList);
            lv_pdf.setAdapter(obj_adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                perm = true;
                getfile(dir);
                obj_adapter = new PDFAdapter(pdfList.this,fileList);
                lv_pdf.setAdapter(obj_adapter);

            }else {
                Toast.makeText(pdfList.this,"Permission has not been granted",Toast.LENGTH_LONG).show();
            }
        }
    }




    public ArrayList<File> getfile(File dir){
        File listFile[] = dir.listFiles();

        if(listFile!=null && listFile.length>0){
            for(int i=0;i<listFile.length;i++){
                if(listFile[i].isDirectory()){
                    getfile(listFile[i]);
                }
                else {
                    boolean booleanpdf = false;
                    if(listFile[i].getName().endsWith(".pdf")){
                        for(int j=0;j<fileList.size();j++){
                            if(fileList.get(j).getName().equals(listFile[i].getName())){
                                booleanpdf = true;
                            }else{

                            }
                        }
                        if(booleanpdf){
                            booleanpdf = false;
                        }else {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }



}