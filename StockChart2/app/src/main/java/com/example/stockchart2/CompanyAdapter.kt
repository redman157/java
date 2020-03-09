package com.example.stockchart2

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*

class CompanyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Notifier {
    private var _Layouts : MutableMap<Int, Int>? = null
    private var _Renderer : MutableMap<Int, Renderer>?= null
    private var recyclerView :RecyclerView?= null
    private var context: Context? = null
    private var adapter: CompanyAdapter?= null

    private object Singleton {
        var instance : CompanyAdapter?= null
    }

    companion object {
        @JvmStatic
        fun getInstance() : CompanyAdapter{
            if (Singleton.instance == null){
                Singleton.instance = CompanyAdapter()
            }
            return Singleton.instance!!
        }
    }

    public fun setActivity(context: Context?){
        this.context = context
    }
    public fun setRecyclerView(recyclerView: RecyclerView?){
        this.recyclerView = recyclerView
    }

    public fun getActivity() : Context?{
        return this.context
    }

    public fun getRecyclerView(): RecyclerView?{
        return this.recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(_Layouts!!.getValue(viewType), null)
        return _Renderer!!.getValue(viewType).onGenerate(view)!!
    }

    override fun getItemCount(): Int {
        var result : Int? = 0

        for (type : Int in _Renderer!!.keys){
            result = result?.plus(_Renderer!!.getValue(type).onCounting())
        }

        return result!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var holder: TypeViewHolder = holder as TypeViewHolder
        _Renderer!![holder.getType()]!!.onBinding(holder, position)
    }

    override fun onDataChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        notifyDataSetChanged()
    }

    @SuppressLint("UseSparseArrays")
    fun addRenderer(
        type: Int,
        renderer: Renderer?
    ) {
        if (_Renderer == null) {
            _Renderer = HashMap<Int,Renderer>()
        }
        if (renderer != null) {
            _Renderer!!.put(type, renderer)
        }
    }

    fun addLayout(type: Int, layout: Int) {
        if (_Layouts == null) {
            _Layouts = HashMap<Int, Int>()
        }
        _Layouts!!.put(type, layout)
    }

    fun addAdapter(adapter: CompanyAdapter){
        this.adapter = adapter
    }
    interface Renderer {
        fun onAssignNotifying(notify: Notifier?)
        fun onGenerate(view: View?): TypeViewHolder?
        fun onBinding(holder: ViewHolder?, position: Int)
        fun onCounting(): Int
    }

}