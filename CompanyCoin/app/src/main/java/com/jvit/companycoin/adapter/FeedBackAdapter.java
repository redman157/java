package com.jvit.companycoin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.api.InfoLikeUser;
import com.jvit.companycoin.api.LikeIdea;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.fragment.FeedBackFragment;
import com.jvit.companycoin.objectbuild.Comment;
import com.jvit.companycoin.object.InfoUserLike;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jvit.companycoin.fragment.HomeFragment.apiClient;
import static com.jvit.companycoin.fragment.HomeFragment.sliderFeedBackAdapter;


public class FeedBackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private SharedPreferences preferencesToken;
    private String token;

    private ArrayList<InfoUserLike> likeArrayList;
    private InfoUserLikeAdapter userLikeAdapter;
    private ArrayList<Comment> listAllComment;
    private final int VIEW_TYPE_ITEM=0, VIEW_TYPE_LOADING=1;

    private TextView textName,textTime,textIdea,textNumLike,textNumCoin;
    private ImageView imgAvatar, imgHeart, imgClose;
    private RecyclerView rcViewInfoIdea;
    private SpannableString spannableContent;
    private Dialog dialog;

    private class ItemIdea extends RecyclerView.ViewHolder{
        // tạo 1 inner class kế thừa từ lớp RecyclerView.ItemIdea
        LinearLayout linearFavorite, linearPost;
        TextView textNameAvatar, textHour, textComment, textToken, textNum;
        ImageView imgAvatar, imgHeart;

        ItemIdea(@NonNull View itemView) {
            super(itemView);

            linearPost = itemView.findViewById(R.id.linearitemPostFragment);
            imgHeart = itemView.findViewById(R.id.imgLikePostFragment);
            textNum = itemView.findViewById(R.id.textNumFavoritePost);
            linearFavorite = itemView.findViewById(R.id.lineFavorite);
            imgAvatar = itemView.findViewById(R.id.imgAvataPost);
            textNameAvatar = itemView.findViewById(R.id.textNameAvataPost);
            textHour = itemView.findViewById(R.id.textDayPost);
            textComment = itemView.findViewById(R.id.textCommentPost);
            textToken = itemView.findViewById(R.id.textNumCoinPost);
        }
    }
    private class LoadMoreIdea extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadMoreIdea(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarLoadedPostAll);
        }
    }

    private SimpleDateFormat format;
    public FeedBackAdapter(Context context, ArrayList<Comment> listAllComment) {
        this.context = context;
        this.listAllComment = listAllComment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment_post, parent, false);
            return new ItemIdea(view);
        }else{
            View view =
                    LayoutInflater.from(context).inflate(R.layout.item_loading_postall, parent,
                            false);
            return new LoadMoreIdea(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        preferencesToken = context.getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, "token_null");
        if (holder instanceof ItemIdea){
            final Comment comment = listAllComment.get(position);
            final ItemIdea itemIdea = (ItemIdea) holder;


            Picasso.get()
                    .load(ApiService.url_path+comment.getAvatar())
                    .fit()
                    .into(itemIdea.imgAvatar);
            itemIdea.textNameAvatar.setText(comment.getNameAvatar());

            itemIdea.textComment.setText(comment.getContent());
            itemIdea.textNum.setText(""+comment.getReactions_count());
            itemIdea.textToken.setText("+"+comment.getToken_amount()+" "+context.getString(R.string.coin));

            try {
                format = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

                Date past = format.parse(comment.getCreate_at());
                Date now = new Date();

                itemIdea.textHour.setText(checkTime(now, past));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!comment.isReacted()) {
                itemIdea.linearFavorite.setBackground(context.getDrawable(R.drawable.custom_comment_favorite));
                itemIdea.imgHeart.setImageResource(R.drawable.ic_favorite_gray);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemIdea.textNum.setTextColor(context.getColor(R.color.gray_dark));
                } else {
                    itemIdea.textNum.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
                }
            }
            else if (comment.getNameAvatar().equals(HomeFragment.name) && !comment.isReacted()){
                itemIdea.linearFavorite.setBackground(context.getDrawable(R.drawable.custom_comment_favorite_me));
                itemIdea.imgHeart.setImageResource(R.drawable.ic_favorite_gray);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemIdea.textNum.setTextColor(context.getColor(R.color.gray_dark));
                } else {
                    itemIdea.textNum.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
                }
            }
            else{
                itemIdea.linearFavorite.setBackground(context.getDrawable(R.drawable.custom_comment_farorite_like));
                itemIdea.imgHeart.setImageResource(R.drawable.icon_favorite_white);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemIdea.textNum.setTextColor(context.getColor(R.color.white));
                }else {
                    itemIdea.textNum.setTextColor(ContextCompat.getColor(context,R.color.white));
                }
            }
            itemIdea.linearFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<LikeIdea> likeIdeaCall = FeedBackFragment.apiClient.LIKE_IDEA_CALL(
                            "Bearer "+token,
                            String.valueOf(comment.getId()));
                    likeIdeaCall.enqueue(new Callback<LikeIdea>() {
                        @Override
                        public void onResponse(Call<LikeIdea> call, Response<LikeIdea> response) {
                            LikeIdea likeIdea = response.body();
                            if (likeIdea!= null){
                                itemIdea.textNum.setText(
                                        (likeIdea.getData().getReactions_count()) +" ");
                                itemIdea.linearFavorite.setBackground(context.getDrawable(R.drawable.custom_comment_farorite_like));
                                itemIdea.imgHeart.setImageResource(R.drawable.icon_favorite_white);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    itemIdea.textNum.setTextColor(context.getColor(R.color.white));
                                }else {
                                    itemIdea.textNum.setTextColor(ContextCompat.getColor(context,R.color.white));
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<LikeIdea> call, Throwable t) {
                            call.cancel();

                        }
                    });
                    view.setOnClickListener(null);

                    apiClient.IDEA_NEW_CALL("Bearer " + token).enqueue(sliderFeedBackAdapter);
                }
            });

            itemIdea.linearPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(comment.getId());
                }
            });


        }else if (holder instanceof LoadMoreIdea){
            LoadMoreIdea loadMoreIdea = (LoadMoreIdea) holder;
            loadMoreIdea.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return listAllComment == null ? 0: listAllComment.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listAllComment.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }
    private SpannableString customTextView(String customText){

        int end = customText.indexOf("(");

        spannableContent = new SpannableString(customText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spannableContent.setSpan(new ForegroundColorSpan(context.getColor(R.color.color_info_num_like)),0,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else {
            spannableContent.setSpan(new ForegroundColorSpan(Color.RED),0,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableContent;
    }

    private void showDialog(int id){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info_idea);

        imgClose = dialog.findViewById(R.id.imgCloseInfoUserLike);
        imgHeart = dialog.findViewById(R.id.imgInfoLikeUserPostIdea);
        imgAvatar = dialog.findViewById(R.id.imgAvataInfoPostIdea);
        textName = dialog.findViewById(R.id.textInfoNamePostIdea);
        textTime = dialog.findViewById(R.id.textInfoTimePostIdea);
        textIdea = dialog.findViewById(R.id.textInfoIdeaPostIdea);
        textNumLike = dialog.findViewById(R.id.textNumLikeUserPostIdea);
        textNumCoin = dialog.findViewById(R.id.textPlusNumCoinPostIdea);
        rcViewInfoIdea = dialog.findViewById(R.id.rcViewUserNameLikePostIdea);


        addInfoUserLike(id);
        LinearLayoutManager layoutManager = new GridLayoutManager(context, 2);
        rcViewInfoIdea.setLayoutManager(layoutManager);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addInfoUserLike(int id){
        final Call<InfoLikeUser> infoLikeCall = FeedBackFragment.apiClient.INFO_LIKE_CALL(
                "Bearer "+token,
                id);
        infoLikeCall.enqueue(new Callback<InfoLikeUser>() {
            @Override
            public void onResponse(Call<InfoLikeUser> call, Response<InfoLikeUser> response) {
                InfoLikeUser infoLikeUser = response.body();
                likeArrayList = new ArrayList<>();
                if (infoLikeUser != null){
                    InfoLikeUser.Data data = infoLikeUser.getData();
                    String content = data.getContent();
                    String sendAt = data.getSent_at();
                    int reactions_count = data.getReactions_count();
                    boolean reacted = data.isReacted();
                    int token = data.getToken_amount();

                    InfoLikeUser.User user = infoLikeUser.getData().getUser();
                    String name = user.getName();
                    String avatar_path = user.getAvatar_path();

                    List<InfoLikeUser.Reactions> reactionsList = data.getReactions();
                    for (InfoLikeUser.Reactions reactions : reactionsList){
                        likeArrayList.add(new InfoUserLike(reactions.getUser_avatar(), reactions.getUser_name()));
                    }
                    if (likeArrayList.size() > 8){
                        ViewGroup.LayoutParams params= rcViewInfoIdea.getLayoutParams();
                        params.height= 400;
                        rcViewInfoIdea.setLayoutParams(params);
                    }
                    userLikeAdapter = new InfoUserLikeAdapter(context, likeArrayList);
                    Picasso.get().load(ApiService.url_path+avatar_path).fit().into(imgAvatar);
                    textName.setText(name);
                    textTime.setText(sendAt);
                    textIdea.setText(content);
                    textNumCoin.setText("+ " +token+ " "+ context.getString(R.string.coin));
                    if (reacted){
                        textNumLike.setText(customTextView(reactions_count+" like (Bạn đã like ý kiến này rồi!)"));
                        imgHeart.setImageResource(R.drawable.icon_favorite_red);
                    }else {
                        textNumLike.setText(reactions_count+" like");
                        imgHeart.setImageResource(R.drawable.ic_favorite_gray);
                    }

                    rcViewInfoIdea.setAdapter(userLikeAdapter);
                }
            }

            @Override
            public void onFailure(Call<InfoLikeUser> call, Throwable t) {
                call.cancel();

            }
        });
    }
    private String checkTime(Date now, Date past){

        long sec = TimeUnit.MILLISECONDS.toSeconds(now.getTime()) - TimeUnit.MILLISECONDS.toSeconds(past.getTime());
        if (sec<0){
            return "vài giây trước";
        }
        if (sec < 60) {
            return sec + " "+context.getString(R.string.sec_ago);
        } else if (sec < 60*60) {
            return (sec/60)  + " "+context.getString(R.string.minute_ago);
        } else if (sec < 24*60*60) {
            return (sec/(60*60)) + " "+context.getString(R.string.hour_ago);
        } else if (sec < 7*24*60*60) {
            return (sec/(24*60*60)) + " "+context.getString(R.string.day_ago);
        } else {
            return (sec / (7 * 24 * 60 * 60)) + " "+context.getString(R.string.week_ago);
        }
    }
}
