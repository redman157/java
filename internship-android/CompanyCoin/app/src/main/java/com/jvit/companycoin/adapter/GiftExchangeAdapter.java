package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.objectbuild.GiftExchange;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GiftExchangeAdapter extends RecyclerView.Adapter<GiftExchangeAdapter.ViewHolder>{
    private Context context;
    private ArrayList<GiftExchange> listGift;
    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onClick(GiftExchange gift);
    }

    public GiftExchangeAdapter(Context context, ArrayList<GiftExchange> listGift) {
        this.context = context;
        this.listGift = listGift;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_giftexchange, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final GiftExchange giftExchange = listGift.get(position);
        Picasso.get()
                .load(ApiService.url_path+giftExchange.getImageGift())
                .fit()
                .into(holder.imgGift);
        holder.textName.setText(giftExchange.getGiftName());
        holder.textInfo.setText(giftExchange.getIntroGift());
        holder.textRemain.setText(context.getString(R.string.remaining)+"  "+giftExchange.getQuantity()+" "+context.getString(R.string.items));

        holder.textExchange.setText(giftExchange.getPriceGift()+" "+context.getString(R.string.coin));
        holder.btnGiftExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(giftExchange);
            }
        });
        if (giftExchange.isRecommend() == true && giftExchange.isNew() == true){
            holder.textIsRecommend.setText("Đề Nghị");
        }

        if (giftExchange.isRecommend() == true){
            holder.textIsRecommend.setText("Đề Nghị");
        }

        if (giftExchange.isNew() == true){
            holder.textIsRecommend.setText("Mới");
        }

    }

    @Override
    public int getItemCount() {
            return listGift.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // tạo int postition
        int position;
        ImageView imgGift;
        RecyclerView recyclerView;
        TextView textInfo, textName, textExchange, textRemain, textIsRecommend;
        LinearLayout btnGiftExchange;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            position = -1;
            textIsRecommend = itemView.findViewById(R.id.textRecommendGiftExchange);
            btnGiftExchange = itemView.findViewById(R.id.linearGiftItemExchange);
            recyclerView = itemView.findViewById(R.id.rcViewGiftExchange);
            imgGift = itemView.findViewById(R.id.imgItemGiftExchange);
            textInfo = itemView.findViewById(R.id.textIntroduceGift);
            textRemain = itemView.findViewById(R.id.textItemGiftExchangeRemain);
            textName = itemView.findViewById(R.id.textGiftName);
            textExchange= itemView.findViewById(R.id.textItemGiftExchangeCoin);
        }
    }
}
