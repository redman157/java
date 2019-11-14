package com.example.templatercview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// frontend
class UserHolder extends Holder {
    TextView textUser, textName;

    public UserHolder(@NonNull View itemView) {
        super(itemView, 0);
        textUser = itemView.findViewById(R.id.textSdt);
        textName = itemView.findViewById(R.id.textName);
    }

    @Override
    int getType() {
        return 0;
    }
}

// backend: Retrofit -> List<User>
class UserRenderer implements Adapter.Renderer {
    List<User> _Users;

    public UserRenderer(List<User> users) {
        _Users = users;
    }

    @Override
    public void onBinding(RecyclerView.ViewHolder holder, int position) {
        UserHolder viewholder = (UserHolder)holder;

        viewholder.textName.setText(_Users.get(position).getName());
        viewholder.textUser.setText(_Users.get(position).getSdt());
    }

    @Override
    public Holder onGenerate(View view) {
        return new UserHolder(view);
    }

    public int onCounting() {
        return _Users.size();
    }
}

class Holder extends RecyclerView.ViewHolder {
    private int _Type;

    public Holder(@NonNull View itemView, int type) {
        super(itemView);
        _Type = type;
    }

    int getType() {
        return _Type;
    }
}

class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    public Adapter(Context context) {
        this.context = context;
    }

    interface Renderer {
        void onBinding(RecyclerView.ViewHolder holder, int position);
        Holder onGenerate(View view);
        int onCounting();
    }

    Map<Integer, Renderer> _Renderers;
    Map<Integer, Integer> _Layouts;

    void addRenderer(int type, Renderer renderer) {
        if (_Renderers == null){
            _Renderers = new HashMap<>();
        }
        _Renderers.put(type, renderer);
    }
    void addLayout(int type, int layout) {
        if (_Layouts == null){
            _Layouts = new HashMap<>();
        }
        _Layouts.put(type, layout);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(_Layouts.get(viewType), null);
        return _Renderers.get(viewType).onGenerate(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Holder holder = (Holder) viewHolder;
        _Renderers.get(holder.getType()).onBinding(holder, position);
    }

    @Override
    public int getItemCount() {
        int result = 0;

        for (Integer type: _Renderers.keySet()) {
               result += _Renderers.get(type).onCounting();
        }
        return result;
    }
}

public class MainActivity extends AppCompatActivity {
    private CandleStickChart candleStickChart;
    private Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> listUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listUser = new ArrayList<>();
        listUser.add(new User("pham son", "hehe"));
        listUser.add(new User("pham son", "hehe"));
        listUser.add(new User("pham son", "hehe"));
        listUser.add(new User("pham son", "hehe"));

        if (adapter == null) {
            adapter = new Adapter(this);
            adapter.addRenderer(0, new UserRenderer(listUser));
            adapter.addLayout(0, R.layout.item_info);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initChart();
        CandleDataSet set1 = getCandleDataSet();

        // create a data object with the datasets
        CandleData data = getCandleData(set1);

        // set data

        candleStickChart.setData(data);
        candleStickChart.notifyDataSetChanged();
        candleStickChart.invalidate();
    }

    private CandleData getCandleData(CandleDataSet set1) {
        CandleData data = null;
        if (data == null){
            data = new CandleData(set1);
        }
        data.notifyDataChanged();
        return data;
    }

    private CandleDataSet getCandleDataSet() {
        ArrayList<CandleEntry> yValsCandleStick = getCandleEntries();


        CandleDataSet set1 = new CandleDataSet(yValsCandleStick, "DataSet 1");

        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(getResources().getColor(R.color.blue));
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(getResources().getColor(R.color.read));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(getResources().getColor(R.color.green));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);
        return set1;
    }

    private ArrayList<CandleEntry> getCandleEntries() {
        ArrayList<CandleEntry> yValsCandleStick= new ArrayList<>();
        yValsCandleStick.add(new CandleEntry(0, 225.0f, 219.84f, 224.94f, 221.07f));
        yValsCandleStick.add(new CandleEntry(1, 228.35f, 222.57f, 223.52f, 226.41f));
        yValsCandleStick.add(new CandleEntry(2, 226.84f,  222.52f, 225.75f, 223.84f));
        yValsCandleStick.add(new CandleEntry(3, 222.95f, 217.27f, 222.15f, 217.88f));
        return yValsCandleStick;
    }

    private void initChart() {
        candleStickChart = findViewById(R.id.chart1);
        candleStickChart.setHighlightPerDragEnabled(true);
        candleStickChart.setDrawBorders(true);
        candleStickChart.setBorderColor(getResources().getColor(R.color.colorPrimaryDark));

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);

    }

}
