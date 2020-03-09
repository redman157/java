package com.example.booklist;


import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{
    ArrayList<Book> listBook;
    private Context context;

    public BookAdapter(ArrayList<Book> listBook, Context context) {
        this.listBook = listBook;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = listBook.get(position);
        holder.textInfo.setText(book.getInfo());
        holder.textTitle.setText(book.getTitle());
        highlightText(book.getHightLight(), holder.textInfo);
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textInfo, textTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textInfo = itemView.findViewById(R.id.textInfo);
            textTitle = itemView.findViewById(R.id.textTitle);
        }
    }
    private void highlightText(String s, TextView textHighLight) {
        SpannableString spannableString = new SpannableString(textHighLight.getText());
        BackgroundColorSpan[] backgroundColorSpan =
                spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);

        for (BackgroundColorSpan bgSpan : backgroundColorSpan) {
            spannableString.removeSpan(bgSpan);
        }
        int indexOfKeyWord = spannableString.toString().indexOf(s);

        while (indexOfKeyWord > 0) {
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOfKeyWord,
                    indexOfKeyWord + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            indexOfKeyWord = spannableString.toString().indexOf(s, indexOfKeyWord + s.length());
        }
        textHighLight.setText(spannableString);
    }
}
