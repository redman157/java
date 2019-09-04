package com.example.lvbasic;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LanguageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Language> listLanguage;

    public LanguageAdapter(Context context, ArrayList<Language> listLanguage) {
        this.context = context;
        this.listLanguage = listLanguage;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return listLanguage.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int pos, View contentView, ViewGroup parent) {

        final ViewHolder holder;
        if(contentView == null){
            contentView = layoutInflater.inflate(R.layout.activity_user, null);
            holder = new ViewHolder();
            holder.imageView = contentView.findViewById(R.id.imgLogo);
            holder.textLanguage =contentView.findViewById(R.id.textLanguage);
            holder.textUser = contentView.findViewById(R.id.textUser);
            contentView.setTag(holder);
        }
        else {
            holder = (ViewHolder) contentView.getTag();
        }

        final Language language = listLanguage.get(pos);

        holder.imageView.setImageResource(language.getImg());
        holder.textUser.setText(language.getName());
        holder.textLanguage.setText(language.getLanguage());

        holder.textUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, holder.textUser.getText().toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(context, language.getName(),Toast.LENGTH_SHORT).show();
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Hình " + pos,Toast.LENGTH_SHORT).show();
            }
        });

        holder.textLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,language.getLanguage(),Toast.LENGTH_SHORT).show();
            }
        });
        return contentView;

    }
    private class ViewHolder{
        ImageView imageView;
        TextView textUser;
        TextView textLanguage;
    }

}
