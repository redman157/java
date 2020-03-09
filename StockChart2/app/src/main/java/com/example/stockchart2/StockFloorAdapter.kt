package com.example.stockchart2


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import kotlin.collections.ArrayList

class StockFloorAdapter(
    private var context: Activity,
    private val layout: Int,

    private var stockFloors: ArrayList<StockFloor>
) : ArrayAdapter<StockFloor?>(context, layout), Filterable {
    private var filterStockFloor: ArrayList<StockFloor>
        get() {
            TODO()
            return this.filterStockFloor
        }
        set(value) {
            this.filterStockFloor = value
        }

    override fun getView(
        position: Int,
        convertView: View?,
        viewGroup: ViewGroup
    ): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layout, null)
        }
        val stockFloor = filterStockFloor[position]
        val name = view!!.findViewById<TextView>(R.id.txtItemStockFloor)
        name.text = stockFloor.getName()
        return view
    }

    override fun getCount(): Int {
        return stockFloors!!.size
    }

    override fun getItem(position: Int): StockFloor? {
        return stockFloors!![position]
    }



   /* init {
        stockFloors = ArrayList()
        filterStockFloor = stockFloors!!
        for (item in stockFloors!!) {
            stockFloors!!.add(item)
        }

    }*/


    private val filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResult = FilterResults()
            val suggestions = ArrayList<StockFloor>()
            for (stockFloor in stockFloors) {
                if (stockFloor.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    suggestions.add(stockFloor)
                }
            }
            val filterResults = FilterResults()
            filterResults.values = suggestions
            filterResults.count = suggestions.size
            return filterResult
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            val filteredList =
                results.values as ArrayList<StockFloor>
            if (results.count > 0) {
                filterStockFloor.clear()
                filterStockFloor.addAll(filteredList)
                notifyDataSetChanged()
            }
        }

    }

    override fun getFilter(): Filter {
        return filter
    }


}