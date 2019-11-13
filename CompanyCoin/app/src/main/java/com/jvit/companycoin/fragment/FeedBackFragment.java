package com.jvit.companycoin.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jvit.companycoin.objectbuild.BuildComment;
import com.jvit.companycoin.objectbuild.Comment;
import com.jvit.companycoin.adapter.FeedBackAdapter;
import com.jvit.companycoin.R;
import com.jvit.companycoin.activity.SuggestActivity;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.IdeaAll;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class FeedBackFragment extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    public FeedBackFragment() {
    }
    private SwipeRefreshLayout refreshPostIdea;
    private RecyclerView rcViewPost;
    private FeedBackAdapter feedBackAllAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Comment> listAllIdea, listNewIdea, listPopularIdea;
    private View view;
    private Button btnAll, btnNew, btnSuggest;
    private LinearLayout linearComment;
    private TextView textTitle, textName;
    public static ImageView imgAvatar;
    public static ApiClient apiClient;
    private SharedPreferences preferencesToken;
    private String token;
    private boolean isLoading = false;
    private int total, current_page, total_pages;
    private int REQUEST_RELOAD = 123;
    private ProgressBar progressBarLoadData;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_post, container, false);
        initView();
        progressBarLoadData.setVisibility(View.VISIBLE);
        rcViewPost.setVisibility(View.GONE);
        preferencesToken = getActivity().getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);
        Log.d("AAA", HomeFragment.avatar == null? "null":HomeFragment.avatar);

        Picasso.get().load( HomeFragment.avatar).into(imgAvatar);
        textName.setText(HomeFragment.name);

        setUpList();

        feedBackAllAdapter = new FeedBackAdapter(getActivity(), listAllIdea);
        layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rcViewPost.setAdapter(feedBackAllAdapter);
        rcViewPost.setLayoutManager(layoutManager);
        addListAll();

        initScrollListener(listAllIdea);

        btnAll.setOnClickListener(this);
        btnSuggest.setOnClickListener(this);
        btnNew.setOnClickListener(this);
        linearComment.setOnClickListener(this);

        setUpRefresh();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    private void setUpRefresh() {
        refreshPostIdea.setOnRefreshListener(this);
        refreshPostIdea.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshPostIdea.post(new Runnable() {
            @Override
            public void run() {

                refreshPostIdea.setRefreshing(true);
                addListAll();
                addListNew();
                addListPopular();
            }
        });
    }

    private void setUpList() {
        if (listAllIdea == null) {
            listAllIdea = new ArrayList<>();

        }
        if (listNewIdea == null) {
            listNewIdea = new ArrayList<>();
            listNewIdea = addListNew();
        }
        if (listPopularIdea == null) {
            listPopularIdea = new ArrayList<>();
            listPopularIdea = addListPopular();
        }
    }

    private void initView() {
        progressBarLoadData = view.findViewById(R.id.progressLoadData);
        refreshPostIdea = view.findViewById(R.id.refreshPostIdea);
        textName = view.findViewById(R.id.textNameAvatarPostComment);
        textTitle = view.findViewById(R.id.textTitleMyPostFragment);
        linearComment = view.findViewById(R.id.lineCommentPostFragment);
        rcViewPost = view.findViewById(R.id.rcViewAllCommentPost);
        btnAll = view.findViewById(R.id.btnAllCommentPost);
        btnNew = view.findViewById(R.id.btnNewCommentPost);
        btnSuggest = view.findViewById(R.id.btnSuggestCommentPost);
        imgAvatar = view.findViewById(R.id.imgAvatarPostComment);
    }


    private void initScrollListener(final ArrayList<Comment> listComment) {
        rcViewPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && listComment.size() > 1) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == listComment.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }
    private void loadMore() {
        if (listAllIdea.size() < total || current_page < total_pages) {

            listAllIdea.add(null);
            feedBackAllAdapter.notifyItemChanged(listAllIdea.size() - 1);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listAllIdea.remove(listAllIdea.size() - 1);
                    int scrollPosition = listAllIdea.size();
                    feedBackAllAdapter.notifyItemRemoved(scrollPosition);
                    loadPage();

                    feedBackAllAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    }

    private void loadPage() {
        current_page = current_page + 1;
        final Call<IdeaAll> ideaAllCall =
                apiClient.IDEA_ALL_PAGE_CALL("Bearer " +token,String.valueOf(current_page));
        ideaAllCall.enqueue(new Callback<IdeaAll>() {
            @Override
            public void onResponse(Call<IdeaAll> call, Response<IdeaAll> response) {
                IdeaAll ideaAll = response.body();
                if (ideaAll!= null) {
                    List<IdeaAll.InfoIdea> infoIdeaList = ideaAll.getIdeaList();
                    for (IdeaAll.InfoIdea infoIdea : infoIdeaList) {
                        listAllIdea.add(new Comment(
                                infoIdea.getId(),
                                infoIdea.getUser().getAvatar_path(),
                                infoIdea.getUser().getName(),
                                infoIdea.getCreated_at(),
                                infoIdea.getContent(),
                                infoIdea.getReacted(),
                                infoIdea.getReactions_count(),
                                infoIdea.getToken_amount()
                        ));

                    }
                }
                feedBackAllAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<IdeaAll> call, Throwable t) {

            }
        });
    }

    private void addListAll() {
        apiClient.IDEA_ALL_CALL("Bearer " + token).enqueue(new Callback<IdeaAll>() {
            @Override
            public void onResponse(Call<IdeaAll> call, Response<IdeaAll> response) {
                IdeaAll ideaAll = response.body();
                if (ideaAll!= null) {
                    List<IdeaAll.InfoIdea> ideaList = ideaAll.getIdeaList();

                    listAllIdea.clear();
                    for (IdeaAll.InfoIdea idea : ideaList) {


                        Comment comment = new BuildComment()
                                .id(idea.getId())
                                .avatar(idea.getUser().getAvatar_path())
                                .nameAvatar(idea.getUser().getName())
                                .create_at(idea.getSent_at())
                                .content(idea.getContent())
                                .reacted(idea.getReacted())
                                .reactions_count(idea.getReactions_count())
                                .token_amount(idea.getToken_amount()).build();
                        listAllIdea.add(comment);
                    }
                }
                IdeaAll.Meta meta = ideaAll.getMeta();
                IdeaAll.PaginationIdea paginationIdea = meta.getPaginationIdea();

                total = paginationIdea.getTotal();
                current_page = paginationIdea.getCurrent_page();
                total_pages = paginationIdea.getTotal_pages();
                feedBackAllAdapter.notifyDataSetChanged();
                refreshPostIdea.setRefreshing(false);
                progressBarLoadData.setVisibility(View.GONE);
                rcViewPost.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<IdeaAll> call, Throwable t) {

            }
        });
    }

    private ArrayList<Comment> addListNew() {
        apiClient.IDEA_NEW_CALL("Bearer " + token).enqueue(new Callback<IdeaAll>() {
            @Override
            public void onResponse(Call<IdeaAll> call, Response<IdeaAll> response) {
                IdeaAll ideaAll = response.body();
                if (ideaAll!= null) {
                    List<IdeaAll.InfoIdea> ideaList = ideaAll.getIdeaList();
                    listNewIdea.clear();
                    for (IdeaAll.InfoIdea idea : ideaList) {
                        Comment comment = new BuildComment()
                                .id(idea.getId())
                                .avatar(idea.getUser().getAvatar_path())
                                .nameAvatar(idea.getUser().getName())
                                .create_at(idea.getSent_at())
                                .content(idea.getContent())
                                .reacted(idea.getReacted())
                                .reactions_count(idea.getReactions_count())
                                .token_amount(idea.getToken_amount())
                                .build();
                        listNewIdea.add(comment);
                    }
                }
                refreshPostIdea.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<IdeaAll> call, Throwable t) {
                refreshPostIdea.setRefreshing(false);
            }
        });
        return listNewIdea;
    }

    private ArrayList<Comment> addListPopular() {
        apiClient.IDEA_POPULAR_CALL("Bearer " + token).enqueue(new Callback<IdeaAll>() {
            @Override
            public void onResponse(Call<IdeaAll> call, Response<IdeaAll> response) {
                IdeaAll ideaAll = response.body();
                if (ideaAll!= null) {
                    List<IdeaAll.InfoIdea> ideaList = ideaAll.getIdeaList();
                    listPopularIdea.clear();
                    for (IdeaAll.InfoIdea idea : ideaList) {
                        listPopularIdea.add(new Comment(idea.getId(),
                                idea.getUser().getAvatar_path(),
                                idea.getUser().getName(), idea.getSent_at(),
                                idea.getContent(), idea.getReacted(), idea.getReactions_count(),
                                idea.getToken_amount()));
                    }
                }
                refreshPostIdea.setRefreshing(false);
            }


            @Override
            public void onFailure(Call<IdeaAll> call, Throwable t) {
                call.cancel();
                refreshPostIdea.setRefreshing(false);

            }
        });
        return listPopularIdea;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAllCommentPost:
                btnAll.setTextColor(getResources().getColor(R.color.white));
                btnNew.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSuggest.setTextColor(getResources().getColor(R.color.gray_dark));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_grey));
                btnNew.setBackground(getResources().getDrawable(R.drawable.custom_button_new_arrial_giftexchange_white));
                btnSuggest.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_white));
                textTitle.setText(getActivity().getResources().getString(R.string.all));

                feedBackAllAdapter = new FeedBackAdapter(getActivity(), listAllIdea);
                layoutManager = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.VERTICAL, false);
                rcViewPost.setAdapter(feedBackAllAdapter);
                rcViewPost.setLayoutManager(layoutManager);
                feedBackAllAdapter.notifyDataSetChanged();
                initScrollListener(listAllIdea);
                break;
            case R.id.btnNewCommentPost:
                btnAll.setTextColor(getResources().getColor(R.color.gray_dark));
                btnNew.setTextColor(getResources().getColor(R.color.white));
                btnSuggest.setTextColor(getResources().getColor(R.color.gray_dark));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_white));
                btnNew.setBackground(getResources().getDrawable(R.drawable.custom_button_new_arrial_giftexchange_gray));
                btnSuggest.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_white));
                textTitle.setText(getActivity().getResources().getString(R.string.new_info));


                feedBackAllAdapter = new FeedBackAdapter(getActivity(), listNewIdea);

                layoutManager = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.VERTICAL, false);
                rcViewPost.setAdapter(feedBackAllAdapter);
                rcViewPost.setLayoutManager(layoutManager);


                break;
            case R.id.btnSuggestCommentPost:
                btnAll.setTextColor(getResources().getColor(R.color.gray_dark));
                btnNew.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSuggest.setTextColor(getResources().getColor(R.color.white));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_white));
                btnNew.setBackground(getResources().getDrawable(R.drawable.custom_button_new_arrial_giftexchange_white));
                btnSuggest.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_grey));
                textTitle.setText(getActivity().getResources().getString(R.string.suggestions));

                feedBackAllAdapter = new FeedBackAdapter(getActivity(), listPopularIdea);


                layoutManager = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.VERTICAL, false);
                rcViewPost.setAdapter(feedBackAllAdapter);
                rcViewPost.setLayoutManager(layoutManager);


                break;
            case R.id.lineCommentPostFragment:
                startActivityForResult(new Intent(getActivity(), SuggestActivity.class),REQUEST_RELOAD);
                break;

        }
    }

    @Override
    public void onRefresh() {
        addListAll();
        addListNew();
        addListPopular();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_RELOAD && resultCode == RESULT_OK && data!= null ){
            boolean reload = data.getBooleanExtra("reload",false);
            if (reload) {
                addListAll();
                addListNew();
                addListPopular();
                refreshPostIdea.setRefreshing(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}