package com.example.stockchart2

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.stockchart2.BuildObject.StockCompany

@Suppress("UNREACHABLE_CODE")
class CompanyCodeRenderer(stockCompanyList : List<StockCompany>): CompanyAdapter.Renderer {
    var _StockCompanyList: List<StockCompany>? = null
    private val _Type = 0
    private var position: Int = 0

    init {
        this._StockCompanyList = stockCompanyList
    }
    override fun onAssignNotifying(notify: Notifier?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGenerate(view: View?): TypeViewHolder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return CompanyCodeHolder(view!!, position)
    }

    override fun onBinding(holder: RecyclerView.ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        this.position = position
        val viewHolder = holder as CompanyCodeHolder

        val stockCompany = _StockCompanyList!![position]

        viewHolder.name!!.text = stockCompany.name
        viewHolder.code!!.text = (stockCompany.code)
        viewHolder.price!!.text = (stockCompany.price.toString() + "")
        viewHolder.status!!.text = (stockCompany.status)
    }

    override fun onCounting(): Int {
        return _StockCompanyList!!.size
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


