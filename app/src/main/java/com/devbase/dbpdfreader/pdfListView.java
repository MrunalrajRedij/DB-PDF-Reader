package com.devbase.dbpdfreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class pdfListView extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<File>();
    pdfAdapter obj_adapter;
    public static int REQUEST_PERMISSION = 1;
    boolean boolean_permission;
    File dir;

    static boolean fromlistBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        lv_pdf = (ListView) findViewById(R.id.listview_pdf);

        dir = new File(Environment.getExternalStorageDirectory().toString());

        permission_fn();

        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ViewPDFFiles.class);
                intent.putExtra("position",position);
                startActivity(intent);
                fromlistBool = true;
            }
        });

    }

    private void permission_fn() {

        if((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

            if((ActivityCompat.shouldShowRequestPermissionRationale(pdfListView.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE))){
            }else{
                ActivityCompat.requestPermissions(pdfListView.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
        }else {
            boolean_permission = true;
            getfile(dir);
            obj_adapter = new pdfAdapter(getApplicationContext(),fileList);
            lv_pdf.setAdapter(obj_adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                boolean_permission = true;
                getfile(dir);
                obj_adapter = new pdfAdapter(getApplicationContext(),fileList);
                lv_pdf.setAdapter(obj_adapter);
            }
            else {
                Toast.makeText(this,"Allow permissions",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList<File> getfile(File dir){
        File listFile[] = dir.listFiles();
        if(listFile!=null && listFile.length>0){

            for (int i = 0; i<listFile.length;i++){
                if(listFile[i].isDirectory()){
                    getfile(listFile[i]);
                }
                else {
                    boolean booleanpdf = false;
                    if(listFile[i].getName().endsWith(".pdf")){
                        for(int j = 0;j<fileList.size();j++){
                            if(fileList.get(j).getName().equals(listFile[i].getName())){
                                booleanpdf = true;
                            }
                            else { }
                        }
                        if(booleanpdf){
                            booleanpdf = false;
                        }
                        else {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }
}