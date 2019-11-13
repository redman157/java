package com.jvit.companycoin.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

//import com.jvit.companycoin.LoadMore;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.AllNofication;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.object.Notification;
import com.jvit.companycoin.adapter.NotificationUserAdapter;
import com.jvit.companycoin.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    public NotificationsFragment(){}
    private RecyclerView rcViewNotification;
    private NotificationUserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Notification> allNotificationList, myNotificationList;
    private View view;
    private Button btnAll, btnMy;
    private ApiClient apiClient;
    private String token;
    private SharedPreferences preferencesToken;
    private String SAVE_TOKEN = "SAVE_TOKEN";
    private int totalAll, currentPageAll, totalPagesAll, totalMy, currentPageMy, totalPagesMy;
    private boolean isLoading= false;
    private int id, token_user, id_user, id_sender ;
    private String created_at, avata_path_user, team_user, avata_path_sender, team_sender, type;
    private String name_user, name_sender, gift_name, infoNotification, message;
    private SwipeRefreshLayout refreshNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        initView();

        preferencesToken = getActivity().getSharedPreferences(SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        if (allNotificationList == null){
            allNotificationList = new ArrayList<>();

        }
        if (myNotificationList == null){
            myNotificationList = new ArrayList<>();
            myNotificationList = addMyNotification();

        }

        userAdapter = new NotificationUserAdapter(getActivity(), allNotificationList);
        rcViewNotification.setAdapter(userAdapter);
        rcViewNotification.setLayoutManager(layoutManager);
        addAllNotification();
        initScrollAllNotification();
        btnAll.setOnClickListener(this);
        btnMy.setOnClickListener(this);
        refreshNotification.setOnRefreshListener(this);
        refreshNotification.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshNotification.post(new Runnable() {
            @Override
            public void run() {
                refreshNotification.setRefreshing(true);
                addAllNotification();
                addMyNotification();
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    private void initView(){
        refreshNotification = view.findViewById(R.id.refreshNotification);
        btnAll = view.findViewById(R.id.btnAllNotifcation);
        btnMy = view.findViewById(R.id.btnMyNotification);
        rcViewNotification = view.findViewById(R.id.rcViewNotification);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }



    private void initScrollAllNotification() {
        rcViewNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && allNotificationList.size() > 1) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == allNotificationList.size() - 1) {
                        Log.d("AAA", "Current total: "+allNotificationList.size()+" TotalAll: "+ totalAll + "/ "+
                                "Current Page: "+ currentPageAll+ "/ "+"Total Page All: "+totalPagesAll);
                        if (allNotificationList.size() < totalAll || currentPageAll < totalPagesAll){
                            allNotificationList.add(null);
                            userAdapter.notifyItemChanged(allNotificationList.size() - 1);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    allNotificationList.remove(allNotificationList.size() - 1);
                                    int scrollPosition = allNotificationList.size();
                                    userAdapter.notifyItemChanged(scrollPosition);
                                    currentPageAll = currentPageAll + 1;

                                    callNotificationPage(allNotificationList,currentPageAll);
                                    userAdapter.notifyDataSetChanged();
                                }
                            },2000);
                        }
                        isLoading = true;
                    }
                }
            }
        });
    }
    private void callNotificationPage(final ArrayList<Notification> list, int page){
        Call<AllNofication> notificationCall = apiClient.ALL_NOTIFICATION_PAGE_CALL("Bearer " +token,String.valueOf(page));
        notificationCall.enqueue(new Callback<AllNofication>() {
            @Override
            public void onResponse(Call<AllNofication> call, Response<AllNofication> response) {
                AllNofication allNofication = response.body();
                if (allNofication != null) {
                    List<AllNofication.Data> dataList = allNofication.getData();
                    if (dataList != null) {
                        for (AllNofication.Data data : dataList) {
                            id = data.getId();
                            token_user = data.getToken_amount();
                            created_at = data.getCreated_at();

                            if (data.getUser() != null) {
                                id_user = data.getUser().getId();
                                name_user = data.getUser().getName();
                                avata_path_user = data.getUser().getAvatar_path();
                                team_user = data.getUser().getTeam();
                            }

                            if (data.getSender() != null) {
                                id_sender = data.getSender().getId();
                                name_sender = data.getSender().getName();
                                avata_path_sender = data.getSender().getAvatar_path();
                                team_sender = data.getSender().getTeam();
                            }
                            type = data.getType();
                            switch (Integer.valueOf(type)) {
                                case 1:
                                    message = data.getCustomData().getMessage();
                                    if (name_sender.equals(HomeFragment.name)) {
                                        infoNotification = "Bạn" + " sent coin to " + name_user + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_transfer,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    if (name_user.equals(HomeFragment.name)) {
                                        infoNotification = name_sender + " sent coin to " + "bạn" + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_transfer,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)) {
                                        infoNotification = name_sender + " sent coin to " + name_user + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_transfer,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    break;
                                case 2:
                                    message = data.getCustomData().getMessage();
                                    if (name_sender.equals(HomeFragment.name)) {
                                        infoNotification = "Bạn" + " posted apiClient new idea " + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_transfer,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    } else {
                                        infoNotification = name_sender + " posted apiClient new idea " + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_idea,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    break;
                                case 3:
                                    if (data.getCustomData().getMessage() == null){
                                        message = "";
                                    }else {
                                        message = data.getCustomData().getMessage();
                                    }
                                    if (name_user.equals(HomeFragment.name)){
                                        infoNotification = "Công ty vừa gửi coin cho bạn ("+ data.getToken_amount()+" "+
                                                getActivity().getResources().getString(R.string.coin)+")";
                                        allNotificationList.add(new Notification(
                                                R.drawable.ic_noti_transfer_from_system,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }else {
                                        infoNotification = "Công ty vừa gửi coin cho "+name_user+"("+ data.getToken_amount()+" "+
                                                getActivity().getResources().getString(R.string.coin)+")";
                                        allNotificationList.add(new Notification(
                                                R.drawable.ic_noti_transfer_from_system,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    break;
                                case 4:
                                    if (data.getToken_amount() != -1) {
                                        Log.d("AAA", "Type 4 Coin: " + data.getToken_amount());
                                    }
                                    break;
                                case 5:
                                    message = data.getCustomData().getMessage();

                                    if (name_user.equals(HomeFragment.name)) {
                                        infoNotification = name_sender + " vừa like bài của " + "bạn";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_first_like,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    if (name_sender.equals(HomeFragment.name)) {
                                        infoNotification = "Bạn" + " vừa like bài của " + name_user;
                                        list.add(new Notification(
                                                R.drawable.ic_noti_first_like,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)) {
                                        infoNotification = name_sender + " vừa like bài của " + name_user;
                                        list.add(new Notification(
                                                R.drawable.ic_noti_first_like,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    break;
                                case 6:
                                    message = data.getCustomData().getMessage();
                                    if (name_user.equals(HomeFragment.name)) {
                                        infoNotification = "Ý kiến của " + "bạn" + " vừa đạt được số lượng like và nhận được coin" + "\n" +
                                                "" + "(" + token_user + " " + getActivity().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_reached_point_reward,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    } else {
                                        infoNotification = "Ý kiến của " + name_user + " vừa đạt được số lượng like và nhận được coin" + "\n" +
                                                "" + "(" + token_user + " " + getActivity().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_reached_point_reward,
                                                created_at,
                                                customTextView(infoNotification),
                                                message));
                                    }
                                    break;
                                case 7:
                                    gift_name = data.getCustomData().getGift_name();
                                    if (name_sender.equals(HomeFragment.name)) {
                                        infoNotification = "Bạn " + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_transfer_from_system,
                                                created_at,
                                                customTextView(infoNotification),
                                                ""));
                                    } else {
                                        infoNotification = name_sender + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                                getActivity().getResources().getString(R.string.coin) + ")";
                                        list.add(new Notification(
                                                R.drawable.ic_noti_transfer_from_system,
                                                created_at,
                                                customTextView(infoNotification),
                                                ""));
                                    }
                                    break;
                            }
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<AllNofication> call, Throwable t) {

            }
        });
    }
    private void loadPage(){
        currentPageAll = currentPageAll + 1;
        Call<AllNofication> notificationCall = apiClient.ALL_NOTIFICATION_PAGE_CALL("Bearer " +token,String.valueOf(currentPageAll));
        notificationCall.enqueue(new Callback<AllNofication>() {
            @Override
            public void onResponse(Call<AllNofication> call, Response<AllNofication> response) {
                AllNofication allNofication = response.body();
                if (allNofication != null){
                    List<AllNofication.Data> dataList = allNofication.getData();
                    for (AllNofication.Data data : dataList){
                        id = data.getId();
                        token_user = data.getToken_amount();
                        created_at = data.getCreated_at();

                        if (data.getUser() != null) {
                            id_user = data.getUser().getId();
                            name_user = data.getUser().getName();
                            avata_path_user = data.getUser().getAvatar_path();
                            team_user = data.getUser().getTeam();
                        }

                        if (data.getSender() != null) {
                            id_sender = data.getSender().getId();
                            name_sender = data.getSender().getName();
                            avata_path_sender = data.getSender().getAvatar_path();
                            team_sender = data.getSender().getTeam();
                        }
                        type = data.getType();
                        switch (Integer.valueOf(type)){
                            case 1:
                                 message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_user.equals(HomeFragment.name)) {
                                    infoNotification = name_sender + " sent coin to " + "bạn" + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 2:
                                message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " posted apiClient new idea " + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = name_sender + " posted apiClient new idea "  + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_idea,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                message = data.getCustomData().getMessage();

                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + "bạn";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_sender.equals(HomeFragment.name)){
                                    infoNotification = "Bạn"+ " vừa like bài của " + name_user;
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + name_user;
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 6:
                                 message = data.getCustomData().getMessage();
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Ý kiến của "+"bạn" +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Ý kiến của "+ name_user +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 7:
                                gift_name = data.getCustomData().getGift_name();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn " + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }else {
                                    infoNotification = name_sender + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }
                                break;
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<AllNofication> call, Throwable t) {

            }
        });
    }

    private void addAllNotification(){
        Call<AllNofication> allNotificationCall = apiClient.ALL_NOTIFICATION_CALL("Bearer " + token);
        allNotificationCall.enqueue(new Callback<AllNofication>() {
            @Override
            public void onResponse(Call<AllNofication> call, Response<AllNofication> response) {
                AllNofication allNofication = response.body();
                if (allNofication!= null){
                    List<AllNofication.Data> dataList = allNofication.getData();
                    AllNofication.Meta meta = allNofication.getMeta();
                    totalAll = meta.getPagination().getTotal();
                    currentPageAll = meta.getPagination().getCurrent_page();
                    totalPagesAll = meta.getPagination().getTotal_pages();
                    for (AllNofication.Data data : dataList){
                        id = data.getId();
                        token_user = data.getToken_amount();
                        created_at = data.getCreated_at();

                        if (data.getUser() != null) {
                            id_user = data.getUser().getId();
                            name_user = data.getUser().getName();
                            avata_path_user = data.getUser().getAvatar_path();
                            team_user = data.getUser().getTeam();
                        }

                        if (data.getSender() != null) {
                            id_sender = data.getSender().getId();
                            name_sender = data.getSender().getName();
                            avata_path_sender = data.getSender().getAvatar_path();
                            team_sender = data.getSender().getTeam();
                        }
                        type = data.getType();
                        switch (Integer.valueOf(type)){
                            case 1:
                                message = data.getCustomData().getMessage();

                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_user.equals(HomeFragment.name)) {
                                    infoNotification = name_sender + " sent coin to " + "bạn" + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 2:
                                message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " posted apiClient new idea " + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = name_sender + " posted apiClient new idea "  + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_idea,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 3:
                                if (data.getCustomData().getMessage() == null){
                                    message = "";
                                }else {
                                    message = data.getCustomData().getMessage();
                                }
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Công ty vừa gửi coin cho bạn ("+ data.getToken_amount()+" "+
                                            getActivity().getResources().getString(R.string.coin)+")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Công ty vừa gửi coin cho "+name_user+"("+ data.getToken_amount()+" "+
                                            getActivity().getResources().getString(R.string.coin)+")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 4:
                                if (data.getToken_amount() != -1) {
                                    Log.d("AAA", "Type 4 Coin: " + data.getToken_amount());
                                }
                                break;
                            case 5:
                                message = data.getCustomData().getMessage();
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + "bạn";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_sender.equals(HomeFragment.name)){
                                    infoNotification = "Bạn"+ " vừa like bài của " + name_user;
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + name_user;
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 6:
                                message = data.getCustomData().getMessage();
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Ý kiến của "+"bạn" +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Ý kiến của "+ name_user +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 7:
                                gift_name = data.getCustomData().getGift_name();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn " + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }else {
                                    infoNotification =name_sender + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    allNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }
                                break;
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                    refreshNotification.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<AllNofication> call, Throwable t) {
                refreshNotification.setRefreshing(false);
            }
        });
    }
    private SpannableString customTextView(String customText){
        SpannableString spannableContent = new SpannableString(customText);

        if ((name_user!= null) && customText.contains(name_user)){
            int beg = customText.indexOf(name_user);
            int end = name_user.length()+ beg;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                spannableContent.setSpan(new ForegroundColorSpan(getActivity().getColor(R.color.text_custom)),beg,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                spannableContent.setSpan(new ForegroundColorSpan(Color.BLUE),beg,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if ((name_sender!= null) &&customText.contains(name_sender)){
            int beg = customText.indexOf(name_sender);
            int end = name_sender.length()+ beg;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                spannableContent.setSpan(new ForegroundColorSpan(getActivity().getColor(R.color.text_custom)),beg,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                spannableContent.setSpan(new ForegroundColorSpan(Color.BLUE),beg,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if ((gift_name!= null)&&customText.contains(gift_name)){
            int beg = customText.indexOf(gift_name);
            int end = gift_name.length()+ beg;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                spannableContent.setSpan(new ForegroundColorSpan(getActivity().getColor(R.color.text_custom)),beg,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                spannableContent.setSpan(new ForegroundColorSpan(Color.RED),beg,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableContent;
    }

    private void initScrollMyNotification() {
        rcViewNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && myNotificationList.size() > 1) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == myNotificationList.size() - 1) {
                        loadMoreMy();
                        isLoading = true;
                    }
                }
            }
        });
    }
    private void loadMoreMy(){
        if (myNotificationList.size() < totalMy || currentPageMy < totalPagesMy){
            myNotificationList.add(null);
            userAdapter.notifyItemChanged(myNotificationList.size() - 1);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myNotificationList.remove(myNotificationList.size() - 1);
                    int scrollPosition = myNotificationList.size();
                    userAdapter.notifyItemChanged(scrollPosition);
                    loadPageMy();
                    userAdapter.notifyDataSetChanged();
                }
            },2000);
        }
    }
    private void loadPageMy(){
        currentPageMy = currentPageMy + 1;
        Call<AllNofication> myNotificationPageCall = apiClient.MY_NOTIFICATION_PAGE_CALL("Bearer " +token,String.valueOf(currentPageMy));
        myNotificationPageCall.enqueue(new Callback<AllNofication>() {
            @Override
            public void onResponse(Call<AllNofication> call, Response<AllNofication> response) {
                AllNofication allNofication = response.body();
                if (allNofication != null){
                    List<AllNofication.Data> dataList = allNofication.getData();
                    for (AllNofication.Data data : dataList){
                        id = data.getId();
                        token_user = data.getToken_amount();
                        created_at = data.getCreated_at();

                        if (data.getUser() != null) {
                            id_user = data.getUser().getId();
                            name_user = data.getUser().getName();
                            avata_path_user = data.getUser().getAvatar_path();
                            team_user = data.getUser().getTeam();
                        }

                        if (data.getSender() != null) {
                            id_sender = data.getSender().getId();
                            name_sender = data.getSender().getName();
                            avata_path_sender = data.getSender().getAvatar_path();
                            team_sender = data.getSender().getTeam();
                        }
                        type = data.getType();
                        switch (Integer.valueOf(type)){
                            case 1:
                                message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_user.equals(HomeFragment.name)) {
                                    infoNotification = name_sender + " sent coin to " + "bạn" + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 2:
                                message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " posted apiClient new idea " + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = name_sender + " posted apiClient new idea "  + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_idea,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 3:
                                if (data.getCustomData().getMessage() == null){
                                    message = "";
                                }else {
                                    message = data.getCustomData().getMessage();
                                }
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Công ty vừa gửi coin cho bạn ("+ data.getToken_amount()+" "+
                                            getActivity().getResources().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Công ty vừa gửi coin cho "+name_user+"("+ data.getToken_amount()+" "+
                                            getActivity().getResources().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 4:
                                break;
                            case 5:
                                message = data.getCustomData().getMessage();

                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + "bạn";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_sender.equals(HomeFragment.name)){
                                    infoNotification = "Bạn"+ " vừa like bài của " + name_user;
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + name_user;
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 6:
                                message = data.getCustomData().getMessage();
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Ý kiến của "+"bạn" +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Ý kiến của "+ name_user +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 7:
                                gift_name = data.getCustomData().getGift_name();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn " + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }else {
                                    infoNotification = name_sender + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }
                                break;
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<AllNofication> call, Throwable t) {

            }
        });
    }
    private ArrayList<Notification> addMyNotification(){
        Call<AllNofication> myNotificationCall = apiClient.MY_NOTIFICATION_CALL("Bearer " + token);
        myNotificationCall.enqueue(new Callback<AllNofication>() {
            @Override
            public void onResponse(Call<AllNofication> call, Response<AllNofication> response) {
                AllNofication myNofication = response.body();
                if (myNofication!= null){
                    List<AllNofication.Data> dataList = myNofication.getData();
                    AllNofication.Meta meta = myNofication.getMeta();
                    totalMy = meta.getPagination().getTotal();
                    currentPageMy = meta.getPagination().getCurrent_page();
                    totalPagesMy = meta.getPagination().getTotal_pages();
                    for (AllNofication.Data data : dataList){
                        id = data.getId();
                        token_user = data.getToken_amount();
                        created_at = data.getCreated_at();

                        if (data.getUser() != null) {
                            id_user = data.getUser().getId();
                            name_user = data.getUser().getName();
                            avata_path_user = data.getUser().getAvatar_path();
                            team_user = data.getUser().getTeam();
                        }

                        if (data.getSender() != null) {
                            id_sender = data.getSender().getId();
                            name_sender = data.getSender().getName();
                            avata_path_sender = data.getSender().getAvatar_path();
                            team_sender = data.getSender().getTeam();
                        }
                        type = data.getType();

                        switch (Integer.valueOf(type)){
                            case 1:
                                message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_user.equals(HomeFragment.name)) {
                                    infoNotification = name_sender + " sent coin to " + "bạn" + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " sent coin to " + name_user + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 2:
                                message = data.getCustomData().getMessage();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn" + " posted apiClient new idea " + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = name_sender + " posted apiClient new idea "  + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_idea,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 3:
                                if (data.getCustomData().getMessage() == null){
                                    message = "";
                                }else {
                                    message = data.getCustomData().getMessage();
                                }
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Công ty vừa gửi coin cho bạn ("+ data.getToken_amount()+" "+
                                            getActivity().getResources().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Công ty vừa gửi coin cho "+name_user+"("+ data.getToken_amount()+" "+
                                            getActivity().getResources().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 4:
                                break;
                            case 5:
                                message = data.getCustomData().getMessage();
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + "bạn";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (name_sender.equals(HomeFragment.name)){
                                    infoNotification = "Bạn"+ " vừa like bài của " + name_user;
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                if (!name_user.equals(HomeFragment.name) && !name_sender.equals(HomeFragment.name)){
                                    infoNotification = name_sender + " vừa like bài của " + name_user;
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_first_like,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 6:
                                message = data.getCustomData().getMessage();
                                if (name_user.equals(HomeFragment.name)){
                                    infoNotification = "Ý kiến của "+"bạn" +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }else {
                                    infoNotification = "Ý kiến của "+ name_user +" vừa đạt được số lượng like và nhận được coin"+"\n" +
                                            ""+"("+token_user+" "+getActivity().getString(R.string.coin)+")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_reached_point_reward,
                                            created_at,
                                            customTextView(infoNotification),
                                            message));
                                }
                                break;
                            case 7:
                                gift_name = data.getCustomData().getGift_name();
                                if (name_sender.equals(HomeFragment.name)) {
                                    infoNotification = "Bạn " + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }else {
                                    infoNotification =name_sender + " vừa đổi quà " + gift_name + "(" + token_user + " " +
                                            getActivity().getResources().getString(R.string.coin) + ")";
                                    myNotificationList.add(new Notification(
                                            R.drawable.ic_noti_transfer_from_system,
                                            created_at,
                                            customTextView(infoNotification),
                                            ""));
                                }
                                break;
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                    refreshNotification.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<AllNofication> call, Throwable t) {
                refreshNotification.setRefreshing(false);
            }
        });
        return myNotificationList;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAllNotifcation:
                btnAll.setTextColor(getResources().getColor(R.color.white));
                btnMy.setTextColor(getResources().getColor(R.color.gray_dark));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_grey));
                btnMy.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_white));
                userAdapter = new NotificationUserAdapter(getActivity(), allNotificationList);
                rcViewNotification.setAdapter(userAdapter);
                rcViewNotification.setLayoutManager(layoutManager);
                userAdapter.notifyDataSetChanged();
                initScrollAllNotification();
                break;
            case R.id.btnMyNotification:
                btnAll.setTextColor(getResources().getColor(R.color.gray_dark));
                btnMy.setTextColor(getResources().getColor(R.color.white));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_white));
                btnMy.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_grey));
                userAdapter = new NotificationUserAdapter(getActivity(), myNotificationList);
                rcViewNotification.setAdapter(userAdapter);
                rcViewNotification.setLayoutManager(layoutManager);
                userAdapter.notifyDataSetChanged();
                initScrollMyNotification();
                break;
        }
    }

    @Override
    public void onRefresh() {
        addAllNotification();
        addMyNotification();
    }
}
