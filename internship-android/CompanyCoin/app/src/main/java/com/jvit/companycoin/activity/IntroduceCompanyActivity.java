package com.jvit.companycoin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jvit.companycoin.adapter.HowToGetAdapter;
import com.jvit.companycoin.object.HowToGet;
import com.jvit.companycoin.adapter.OurLeaderAdapter;
import com.jvit.companycoin.R;

import java.util.ArrayList;

public class IntroduceCompanyActivity extends AppCompatActivity {

    private RecyclerView rcViewFeature, rcViewUserLeader;
    private HowToGetAdapter featureAdapter;
    private OurLeaderAdapter ourLeaderAdapter;
    private RecyclerView.LayoutManager layoutManagerFeature, layoutManagerLeader;
    private ArrayList<HowToGet> listFeature, listOutLeader;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_company);

        initView();
        addListFeature();
        addListLeader();

        ourLeaderAdapter = new OurLeaderAdapter(IntroduceCompanyActivity.this, listOutLeader);
        featureAdapter = new HowToGetAdapter(IntroduceCompanyActivity.this, listFeature);

        rcViewUserLeader.setAdapter(ourLeaderAdapter);
        rcViewUserLeader.setLayoutManager(layoutManagerLeader);
        rcViewFeature.setAdapter(featureAdapter);
        rcViewFeature.setLayoutManager(layoutManagerFeature);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initView(){
        btnBack = findViewById(R.id.img_backIntroduceCompany);
        rcViewFeature = findViewById(R.id.rcViewFeature);
        rcViewUserLeader = findViewById(R.id.rcViewOurLeader);
        layoutManagerFeature = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL, false);
        layoutManagerLeader = new GridLayoutManager(this, 2,GridLayoutManager.VERTICAL, false);
    }
    private void addListFeature(){
        listFeature = new ArrayList<>();
        listFeature.add(new HowToGet(R.drawable.ic_feature_introduce_gift,"Lịch Sử Giao Dịch","Lịch sử giao dịch đều được lưu trữ và chia sẻ minh bạch đến toàn bộ nhân viên JV-IT."));
        listFeature.add(new HowToGet(R.drawable.ic_feature_introduce_gift,"Phúc Lợi Thú Vị","Nhân viên công ty JV-IT sẽ được nhận nhiều phúc lợi thú vị từ hệ thống JVC."));
        listFeature.add(new HowToGet(R.drawable.ic_feature_introduce_gift,"Dễ Sử Dụng","Sử dụng đơn giản và nhanh khiến các giao dịch được hoàn tất trong thời gian ngắn nhất."));
        listFeature.add(new HowToGet(R.drawable.ic_feature_introduce_gift,"Thường Xuyên Cập Nhật","Đội ngũ xây dựng JVC sẽ luôn cập nhật và phát triển hệ thống ngày càng tốt hơn."));
        listFeature.add(new HowToGet(R.drawable.ic_feature_introduce_gift,"Bảo Mật","JVC cam kết xây dựng bảo mật tốt nhất và chỉ có thể truy cập tại công ty JV-IT."));
        listFeature.add(new HowToGet(R.drawable.ic_feature_introduce_gift,"Phản Hồi Nhanh","Hệ thống hỗ trợ tiếp nhận và phản hồi ý kiến trực tuyến nhanh nhất có thể cho nhân viên."));
    }
    private void addListLeader(){
        listOutLeader = new ArrayList<>();
        listOutLeader.add(new HowToGet(R.drawable.avata, "RUAN INOSE","Advisor, Sale Manager"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Mashashi Mitani","CEO"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Trần Tấn Đạt","CTO, Advisor, Blockchain Dev"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Nguyễn Hữu Vĩnh","Advisor, Lead Operator"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Lê Minh Tân","Content, Lead Designer"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Nguyễn Huy Hoàng","Frontend Dev"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Phạm Thị Hương","Frontend Dev"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Nguyễn Trí Tân","Blockchain Dev, Backend Dev"));

        listOutLeader.add(new HowToGet(R.drawable.avata, "Dương Văn Tân","Backend Dev"));
        listOutLeader.add(new HowToGet(R.drawable.avata, "Nguyễn Thị Ngọc Tú","Frontend Dev"));

    }

}
