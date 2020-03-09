package com.example.stockchart2

import android.widget.Filter


class PlanetFilter private constructor(): Filter(){

    init {

    }

    private object Holder{
        val instance = PlanetFilter()
    }

    companion object{
        @JvmStatic
        fun getInstance() : PlanetFilter{
            return Holder.instance
        }
    }

    private var stockFloors : ArrayList<StockFloor>
        get() {
            TODO()
            return this.stockFloors
        }
        set(value) {
            this.stockFloors = value
        }

    private var filterStockFloor: ArrayList<StockFloor>
        get() {
            TODO()
            return this.filterStockFloor
        }
        set(value) {
            this.filterStockFloor = value
        }

    private var adapter : StockFloorAdapter
        get() {
            TODO()
            return this.adapter
        }
        set(value) {
            this.adapter = value
        }

    override fun convertResultToString(resultValue: Any): CharSequence {
        return (resultValue as StockFloor).getName()
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        val filterResult = FilterResults()
        val suggestions =
            ArrayList<StockFloor>()
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
        val filteredList = results.values as ArrayList<StockFloor>
        if (results.count > 0) {
            filterStockFloor.clear()
            filterStockFloor.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }
    }
}
