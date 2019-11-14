package com.example.templatercview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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

public class MainActivity extends AppCompatActivity implements OnChartGestureListener {
    private CandleStickChart candleStickChart;
    private Adapter adapter;
    private boolean moveToLastEntry = true;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> listUser;
    private CandleDataSet dataSet;
    private CandleData data;
    private ArrayList<CandleEntry> candleEntries;
    private CandleEntry entry;

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
        addEntry();


    }
    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void addEntry(){
        dataSet = initCandleDataSet();
        // create a data object with the datasets
        CandleData data = initCandleData(dataSet);
        data.notifyDataChanged();
        // set data
        candleStickChart.setData(data);
        candleStickChart.getDescription().setEnabled(false);
        candleStickChart.setOnChartGestureListener(this);
        candleStickChart.notifyDataSetChanged();
        // limit the number of visible entries
        candleStickChart.setVisibleXRangeMaximum(10);
        // candleStickChart.setVisibleYRange(30, AxisDependency.LEFT);

        if (moveToLastEntry) {
            // move to the latest entry
            candleStickChart.moveViewToX(data.getEntryCount());
        }
        candleStickChart.invalidate();
    }
    private CandleData initCandleData(CandleDataSet dataSet) {
        if (data == null){
            data = new CandleData(dataSet);
        }
        data.notifyDataChanged();
        return data;
    }

    private CandleDataSet initCandleDataSet() {
        addDataSet();
        return createSet();
    }

    private void addDataSet() {
        if (candleEntries == null) {
            candleEntries = new ArrayList<>();
            candleEntries.add(new CandleEntry(0, 225.0f, 219.84f, 224.94f, 221.07f));
            candleEntries.add(new CandleEntry(1, 228.35f, 222.57f, 223.52f, 226.41f));
            candleEntries.add(new CandleEntry(2, 226.84f,  222.52f, 225.75f, 223.84f));
            candleEntries.add(new CandleEntry(3, 222.95f, 217.27f, 222.15f, 217.88f));
            candleEntries.add(new CandleEntry(4, 225.0f, 219.84f, 224.94f, 221.07f));
            candleEntries.add(new CandleEntry(5, 228.35f, 222.57f, 223.52f, 226.41f));
            candleEntries.add(new CandleEntry(6, 226.84f,  222.52f, 225.75f, 223.84f));
            candleEntries.add(new CandleEntry(7, 222.95f, 217.27f, 222.15f, 217.88f));
            candleEntries.add(new CandleEntry(8, 225.0f, 219.84f, 224.94f, 221.07f));
            candleEntries.add(new CandleEntry(9, 228.35f, 222.57f, 223.52f, 226.41f));
            candleEntries.add(new CandleEntry(10, 226.84f,  222.52f, 225.75f, 223.84f));
            candleEntries.add(new CandleEntry(11, 222.95f, 217.27f, 222.15f, 217.88f));
            candleEntries.add(new CandleEntry(12, 225.0f, 219.84f, 224.94f, 221.07f));
        }
//        setData();
    }

    private CandleDataSet createSet() {
        if (this.dataSet == null) {
            dataSet = new CandleDataSet(candleEntries, "DataSet 1");
        }

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.getHoloBlue());

        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false);

        dataSet.setColor(Color.rgb(80, 80, 80));

        dataSet.setShadowColor(getResources().getColor(R.color.blue));
        dataSet.setShadowWidth(0.8f);

        dataSet.setDecreasingColor(getResources().getColor(R.color.read));
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);

        dataSet.setIncreasingColor(getResources().getColor(R.color.green));
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);

        dataSet.setNeutralColor(Color.LTGRAY);
        dataSet.setDrawValues(false);

        return dataSet;
    }


    private void setData(float x, float shadowH, float shadowL, float open, float close) {
        entry = new CandleEntry(x, shadowH, shadowL, open,close);
        candleEntries.add(entry);
    }
/*
    private ArrayList<CandleEntry> limitData(ArrayList<CandleEntry> limitList){
        ArrayList<CandleEntry> tempList = new ArrayList<>();
        if (limitList.size() > 70){
            // nhận data

            limitList.clear();

        }
    }*/


    private void initChart() {
        candleStickChart = findViewById(R.id.chart1);
//        candleStickChart.setOnChartValueSelectedListener(entry);
        // enable touch gestures
        candleStickChart.setTouchEnabled(true);
        // enable scaling and dragging
        candleStickChart.setDragEnabled(true);
        candleStickChart.setScaleEnabled(true);
        candleStickChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        candleStickChart.setPinchZoom(false);

        candleStickChart.setBackgroundColor(Color.LTGRAY);

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

    private Thread thread;
    private void feedMultipe(){
        if (thread != null) {
            thread.interrupt();
        }
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        moveToLastEntry = false;
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
