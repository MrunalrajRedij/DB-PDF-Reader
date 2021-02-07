package com.devbase.dbpdfreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PDFAdapter extends ArrayAdapter<File> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;

    public PDFAdapter(Context context, ArrayList<File> al_pdf) {
        super(context, R.layout.adapter_pdf,al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(al_pdf.size()>0){
            return al_pdf.size();
        }
        else return 1;
    }


    @Override
    public View getView(final int position,View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_filename = convertView.findViewById(R.id.tv_name);
            viewHolder.size = convertView.findViewById(R.id.size);
            viewHolder.date = convertView.findViewById(R.id.date);
            convertView.setTag(viewHolder);

        }
        else {
            viewHolder =(ViewHolder) convertView.getTag();
        }

        viewHolder.tv_filename.setText(al_pdf.get(position).getName());



        long fileL = al_pdf.get(position).length();
        long size1 = fileL/1024;
        String size;
        if(size1 >= 1024){
            size = (size1/1024)+" Mb";
        }else {
            size = size1+" Kb";
        }
        viewHolder.size.setText(size);


        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        viewHolder.date.setText(sdf.format(al_pdf.get(position).lastModified()));


        return convertView;
    }


    public class ViewHolder{
        TextView date;
        TextView size;
        TextView tv_filename;
    }

}
