package com.example.expandablelistview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ExpandableListView expandableListView;
    CustomExpandableListView customExpandableListView;
    HashMap<String, ArrayList<Flag>> expandableFlag;
    ArrayList<String> listHeader;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = findViewById(R.id.expandableListView);
        imageView = findViewById(R.id.imgselection);

        expandableFlag = getData();
        listHeader = new ArrayList<>(expandableFlag.keySet());
        customExpandableListView  = new CustomExpandableListView(
                MainActivity.this,
                listHeader,
                expandableFlag);

        expandableListView.setAdapter(customExpandableListView);
        // nếu mở chỉ mở 1
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }
    public HashMap<String, ArrayList<Flag>> getData(){
        HashMap<String, ArrayList<Flag>> expandableFlag = new HashMap<String, ArrayList<Flag>>();

        ArrayList<Flag> Asia = new ArrayList<>();
        Asia.add(new Flag(R.drawable.afghanistan, "Afghanistan"));
        Asia.add(new Flag(R.drawable.china, "China"));
        Asia.add(new Flag(R.drawable.india, "India"));
        Asia.add(new Flag(R.drawable.pakistan, "Pakistan"));

        ArrayList<Flag> Africa = new ArrayList<>();
        Africa.add(new Flag(R.drawable.south, "South Africa"));

        ArrayList<Flag> North = new ArrayList<>();
        North.add(new Flag(R.drawable.canada, "Canada"));

        ArrayList<Flag> South = new ArrayList<>();
        South.add(new Flag(R.drawable.argentina, "Argentina"));

        expandableFlag.put("ASIA", Asia);
        expandableFlag.put("AFRICA",Africa);
        expandableFlag.put("NORTH AMERICA", North);
        expandableFlag.put("SOUTH AMERICA", South);
        return expandableFlag;
    }

}
