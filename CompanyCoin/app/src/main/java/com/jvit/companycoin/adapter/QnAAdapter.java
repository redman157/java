package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.object.QnA;
import com.jvit.companycoin.R;

import java.util.ArrayList;

public class QnAAdapter extends RecyclerView.Adapter<QnAAdapter.ViewHolder> {
    private Context context;
    private ArrayList<QnA> listQnA;

    public QnAAdapter(Context context, ArrayList<QnA> listQnA) {
        this.context = context;
        this.listQnA = listQnA;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qanda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final QnA qna = listQnA.get(position);
        holder.imgIcon.setOnClickListener(new View.OnClickListener() {
            private boolean flag = true;
            @Override
            public void onClick(View view) {
                if (flag) {
                    holder.imgIcon.setImageResource(R.drawable.sub);
                    holder.textReply.setVisibility(View.VISIBLE);
                    holder.view.setVisibility(View.VISIBLE);
                    holder.textReply.setText(qna.getReply());
                    flag = false;
                }else {
                    flag = true;
                    holder.imgIcon.setImageResource(R.drawable.add);
                    holder.textReply.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);
                    holder.textReply.setText(qna.getReply());
                }
            }
        });
        holder.imgIcon.setImageResource(qna.getIcon());
        holder.textQuestion.setText(qna.getQuestion());
    }

    @Override
    public int getItemCount() {
        return listQnA.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textQuestion, textReply;
        ImageView imgIcon;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.viewQNA);
            imgIcon = itemView.findViewById(R.id.imgIconQNA);
            textQuestion = itemView.findViewById(R.id.textQuestionQNA);
            textReply = itemView.findViewById(R.id.textReplyQNA);
        }
    }
}
