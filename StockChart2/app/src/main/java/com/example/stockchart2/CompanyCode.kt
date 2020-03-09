package com.example.stockchart2

import android.content.Context
import android.view.View
import com.example.stockchart2.BuildObject.StockCompany
import java.util.*

@Suppress("UNREACHABLE_CODE")
class CompanyCode(context: Context) : TemplateCompanyCode() {
    private var context : Context?= null
    init {
        this.context = context
    }


    private var adapter: CompanyAdapter? = null
    private val stockCompanies: ArrayList<StockCompany>? = null
    override fun addRender() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (adapter != null) {
            adapter!!.addRenderer(0, stockCompanies?.let { CompanyCodeRenderer(it) })
            adapter!!.addLayout(0, R.layout.item_name_company)
            adapter!!.addAdapter(adapter!!)
        }
    }

    override fun addAdapter() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (adapter == null) {
            adapter = CompanyAdapter()
        }
    }

    override fun addList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (stockCompanies == null) {
            stockCompanies = ArrayList<StockCompany>()
        }
        val stockCompany: StockCompany = StockCompany.Builder()
            .price(129.2)
            .code("VNM1")
            .name("VINAMILK")
            .status("đang hoạt động")
            .build()
        val stockCompany1: StockCompany = StockCompany.Builder()
            .price(129.3)
            .code("AAA2")
            .name("hehe")
            .status("đang hoạt động")
            .build()
        val stockCompany2: StockCompany = StockCompany.Builder()
            .price(129.3)
            .code("AAA3")
            .name("hehe")
            .status("đang hoạt động")
            .build()
        stockCompanies.add(stockCompany)
        stockCompanies.add(stockCompany1)
        stockCompanies.add(stockCompany2)
    }

    override fun showView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (CompanyActivity.getInstance().getRc() == null || CompanyActivity.getInstance().getLm() == null) {
            return
        }
        CompanyActivity.getInstance().getRc().visibility = View.VISIBLE
        CompanyActivity.getInstance().getRc().adapter = adapter
        CompanyActivity.getInstance().getRc().layoutManager = CompanyActivity.getInstance().getLm()
    }
}