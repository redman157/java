package com.example.templatercview.lib_recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.templatercview.lib_recycleview.Holder;

import java.util.HashMap;
import java.util.Map;
public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    public Adapter(Context context) {
        this.context = context;
    }

    private Map<Integer, Renderer> _Renderer;
    private Map<Integer, Integer> _Layouts;

    public void addRenderer(int type, Renderer renderer) {
        if (_Renderer == null){
            _Renderer = new HashMap<>();
        }
        _Renderer.put(type, renderer);
    }
    public void addLayout(int type, int layout){
        if (_Layouts == null){
            _Layouts = new HashMap<>();
        }
        _Layouts.put(type, layout);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(_Layouts.get(viewType), null);
        return _Renderer.get(viewType).onGenerate(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Holder holder = (Holder) viewHolder;
        _Renderer.get(holder.getType()).onBinding(holder, position);
    }

    @Override
    public int getItemCount() {
        int result = 0;

        for (Integer type: _Renderer.keySet()) {
            result += _Renderer.get(type).onCounting();
        }
        return result;
    }

}
