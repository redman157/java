package com.jvit.companycoin.fragment.sliderItemFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.api.InfoLikeUser;
import com.jvit.companycoin.api.LikeIdea;
import com.jvit.companycoin.adapter.InfoUserLikeAdapter;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.object.InfoUserLike;
import com.jvit.companycoin.object.PostSlider;
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

public class ItemFeedBackFragment extends Fragment {
    public ItemFeedBackFragment(){}
    private View view, viewRC;
    private SharedPreferences preferencesToken;
    private String token;
    private TextView textTime, textName, textDescription, textNum, textToken;
    private ImageView iconFeedBack, imgHeart;
    private int id, reactions_count, token_amount;
    private boolean reaction;
    private String imagePost,namePost, timeIdeaPost, commentPost;
    private PostSlider home;
    private LinearLayout linearFavorite, sliderPostFragment;
    private SimpleDateFormat format;
    private InfoUserLikeAdapter likeAdapter;
    private ArrayList<InfoUserLike> likeArrayList;
    private TextView textNameDialog,textTimeDialog,textIdeaDialog,textNumLikeDialog,textNumCoinDialog;
    private ImageView imgAvatarDialog, imgHeartDialog, imgCloseDialog;
    private RecyclerView rcViewInfoIdeaDialog;
    private SpannableString spannableContent;
    private Dialog dialog;
    private final static String ID = "id";
    private final static String REACTION = "reaction";
    private final static String REACTION_COUNT = "reactions_count";
    private final static String TOKEN_AMOUNT = "token_amount";
    private final static String IMAGE = "image";
    private final static String NAME = "name";
    private final static String DATE = "date";
    private final static String COMMENT = "comment";
    private final static String DATA_POST = "data_post";


    public static ItemFeedBackFragment newInstance(PostSlider postSlider){
        ItemFeedBackFragment itemFeedBackFragment = new ItemFeedBackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID, postSlider.getId());
        bundle.putBoolean(REACTION, postSlider.isReaction());
        bundle.putInt(REACTION_COUNT, postSlider.getReactions_count());
        bundle.putInt(TOKEN_AMOUNT, postSlider.getToken_amount());
        bundle.putString(IMAGE, postSlider.getAvatar());
        bundle.putString(NAME, postSlider.getName());
        bundle.putString(DATE, postSlider.getSent_at());
        bundle.putString(COMMENT, postSlider.getContent());
        bundle.putSerializable(DATA_POST, postSlider);
        itemFeedBackFragment.setArguments(bundle);
        return itemFeedBackFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt(ID,0);
        token_amount = getArguments().getInt(TOKEN_AMOUNT,0);
        reactions_count = getArguments().getInt(REACTION_COUNT,0);
        imagePost = getArguments().getString(IMAGE);
        namePost = getArguments().getString(NAME);
        timeIdeaPost = getArguments().getString(DATE);
        reaction = getArguments().getBoolean(REACTION);
        commentPost = getArguments().getString(COMMENT);
        home = (PostSlider) getArguments().getSerializable(DATA_POST);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null){
            return view;
        }

        view = inflater.inflate(R.layout.slide_postidea, container, false);
        initView();
        preferencesToken = getContext().getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);

        try {
            format = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
            Date now = new Date();
            Date past = format.parse(timeIdeaPost);
            textTime.setText(checkTime(now, past));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get().load(ApiService.url_path+imagePost).fit().into(iconFeedBack);
        textDescription.setText(commentPost);
        textName.setText(namePost);
        textToken.setText("+"+token_amount+getResources().getString(R.string.coin));
        textNum.setText(""+reactions_count);

        if (!reaction) {
            linearFavorite.setBackground(getActivity().getDrawable(R.drawable.custom_comment_favorite));
            imgHeart.setImageResource(R.drawable.ic_favorite_gray);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textNum.setTextColor(getActivity().getColor(R.color.gray_dark));
            } else {
                textNum.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_dark));
            }
        } else{
            linearFavorite.setBackground(getActivity().getDrawable(R.drawable.custom_comment_farorite_like));
            imgHeart.setImageResource(R.drawable.icon_favorite_white);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textNum.setTextColor(getActivity().getColor(R.color.white));
            }else {
                textNum.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
            }
        }

        linearFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<LikeIdea> likeIdeaCall =
                        HomeFragment.apiClient.LIKE_IDEA_CALL(
                                "Bearer "+token,
                                String.valueOf(id));
                likeIdeaCall.enqueue(new Callback<LikeIdea>() {
                    @Override
                    public void onResponse(Call<LikeIdea> call, Response<LikeIdea> response) {
                        LikeIdea likeIdea = response.body();
                        if (likeIdea!= null){
                            textNum.setText(
                                    (likeIdea.getData().getReactions_count()) +" ");
                            linearFavorite.setBackground(getActivity().getDrawable(R.drawable.custom_comment_farorite_like));
                            imgHeart.setImageResource(R.drawable.icon_favorite_white);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                textNum.setTextColor(getActivity().getColor(R.color.white));
                            }else {
                                textNum.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<LikeIdea> call, Throwable t) {

                    }
                });
                view.setOnClickListener(null);
            }
        });

        sliderPostFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(id);

            }
        });

        return view;
    }
    private String checkTime(Date now, Date past){
        long sec = TimeUnit.MILLISECONDS.toSeconds(now.getTime()) - TimeUnit.MILLISECONDS.toSeconds(past.getTime());
        if (sec < 0){
            return "vài giây trước";
        }
        if (sec < 60) {
            return sec + " "+getResources().getString(R.string.sec_ago);
        } else if (sec < 60*60) {
            return (sec/60) + " "+getResources().getString(R.string.minute_ago);
        } else if (sec < 24*60*60) {
            return (sec/(60*60)) + " "+getResources().getString(R.string.hour_ago);
        } else if (sec < 7*24*60*60) {
            return (sec/(24*60*60)) + " "+getResources().getString(R.string.day_ago);
        } else {
            return (sec / (7 * 24 * 60 * 60)) + " "+getResources().getString(R.string.week_ago);
        }
    }
    private void initView() {

        sliderPostFragment = view.findViewById(R.id.itemSliderPostFragment);
        linearFavorite = view.findViewById(R.id.lineFavoriteGopY);
        textTime = view.findViewById(R.id.textDayPostHome);
        textName = view.findViewById(R.id.textItemNamePostHome);
        iconFeedBack = view.findViewById(R.id.imgItemPostHome);
        imgHeart = view.findViewById(R.id.imgHeartGopY);
        textDescription = view.findViewById(R.id.textPostingPostHome);
        textNum = view.findViewById(R.id.textReactionPostHomeGopY);
        textToken = view.findViewById(R.id.textTokenPostHome);
    }

    private SpannableString customTextView(String customText){
        int end = customText.indexOf("(");

        spannableContent = new SpannableString(customText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spannableContent.setSpan(new ForegroundColorSpan(getActivity().getColor(R.color.color_info_num_like)),0,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableContent.setSpan(new ForegroundColorSpan(Color.RED),0,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableContent;
    }

    public void showDialog(int id){
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info_idea);

        viewRC = dialog.findViewById(R.id.viewTopRecivewPostInfoIdea);
        imgCloseDialog = dialog.findViewById(R.id.imgCloseInfoUserLike);
        imgHeartDialog = dialog.findViewById(R.id.imgInfoLikeUserPostIdea);
        imgAvatarDialog = dialog.findViewById(R.id.imgAvataInfoPostIdea);
        textNameDialog = dialog.findViewById(R.id.textInfoNamePostIdea);
        textTimeDialog = dialog.findViewById(R.id.textInfoTimePostIdea);
        textIdeaDialog = dialog.findViewById(R.id.textInfoIdeaPostIdea);
        textNumLikeDialog = dialog.findViewById(R.id.textNumLikeUserPostIdea);
        textNumCoinDialog = dialog.findViewById(R.id.textPlusNumCoinPostIdea);
        rcViewInfoIdeaDialog = dialog.findViewById(R.id.rcViewUserNameLikePostIdea);


        addInfoUserLike(id);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rcViewInfoIdeaDialog.setLayoutManager(layoutManager);
        viewRC.setVisibility(View.VISIBLE);
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void addInfoUserLike(int id){
        final Call<InfoLikeUser> infoLikeCall = HomeFragment.apiClient.INFO_LIKE_CALL(
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
                    likeAdapter = new InfoUserLikeAdapter(getContext(), likeArrayList);
                    Picasso.get().load(ApiService.url_path+avatar_path).into(imgAvatarDialog);
                    textNameDialog.setText(name);
                    textTimeDialog.setText(sendAt);
                    textIdeaDialog.setText(content);
                    textNumCoinDialog.setText("+ " +token+ " "+getActivity().getResources().getString(R.string.coin));
                    if (reacted){
                        textNumLikeDialog.setText(customTextView(reactions_count+" like (Bạn đã like ý kiến này rồi!)"));
                        imgHeartDialog.setImageResource(R.drawable.icon_favorite_red);
                    }else {
                        textNumLikeDialog.setText(reactions_count+" like");
                        imgHeartDialog.setImageResource(R.drawable.ic_favorite_gray);
                    }

                    rcViewInfoIdeaDialog.setAdapter(likeAdapter);
                    dialog.show();

                }
            }

            @Override
            public void onFailure(Call<InfoLikeUser> call, Throwable t) {

            }
        });
    }
}
