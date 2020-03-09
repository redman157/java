package com.example.stockchart2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.stockchart2.BuildObject.StockCompany

class CompanyActivity : AppCompatActivity() {
    private var listCompany : RecyclerView? = null
    private var stockCompany : StockCompany? = null
    private var txtViewChooseStockFloor: AutoCompleteTextView ?= null
    private var stockFloors : ArrayList<StockFloor>? = null
    private var rcCompany :RecyclerView?= null
    private val stockCompanies: ArrayList<StockCompany>? = null
    private val companyCodeAdapter: CompanyAdapter? = null
    private var stockFloorAdapter : StockFloorAdapter? = null
    private var rcViewSearchCompany: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var stockFloor :StockFloor?= null

    private object Singleton {
        var instance : CompanyActivity?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_namecompany)
        txtViewChooseStockFloor = this.findViewById(R.id.txtViewChooseStockFloor)
        rcCompany = findViewById(R.id.rcViewSearchCompany)
        addSearch()
        addSearchCompany()

        CompanyAdapter.getInstance().setActivity(this@CompanyActivity)
        CompanyAdapter.getInstance().setRecyclerView(listCompany)
    }

    companion object {
        @JvmStatic
        fun getInstance() : CompanyActivity{
            if (Singleton.instance == null){
                Singleton.instance = CompanyActivity()
            }
            return Singleton.instance!!
        }
    }
    fun setRc(recyclerView: RecyclerView){
        this.rcViewSearchCompany  = recyclerView
    }

    fun setLm(layoutManager: RecyclerView.LayoutManager){
        this.layoutManager = layoutManager
    }

    fun getRc(): RecyclerView{
        return this.rcViewSearchCompany!!
    }

    fun getLm() : RecyclerView.LayoutManager{
        return this.layoutManager!!
    }


    private fun addStockFloor() {
        if (stockFloors == null) {
            stockFloors = ArrayList<StockFloor>()
        }
        stockFloors!!.add(StockFloor("HOSE"))
        stockFloors!!.add(StockFloor("VN30"))
    }

    private fun addSearch() {
        if (stockFloorAdapter == null) {
            addStockFloor()
            stockFloorAdapter =
                 StockFloorAdapter(this, R.layout.item_stock_floor, stockFloors!!)
            txtViewChooseStockFloor!!.setAdapter(stockFloorAdapter)
        }
    }


    private fun isCompany(charSequence: CharSequence): Boolean {
        var check = true
        for (item : StockFloor in stockFloors!!) {
            if (charSequence.toString() != item.getName()) {
                check = false
            }
        }
        return check
    }
    private fun addSearchCompany(){
        txtViewChooseStockFloor?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (charSequence.isEmpty() || !isCompany(charSequence)) {
                    rcCompany!!.visibility = View.GONE
                }

            }

            override fun afterTextChanged(editable: Editable) {
                when (editable.toString()) {
                    "VN30" -> {
                        stockCompanies?.clear()
                        if (companyCodeAdapter == null) {
                            val companyCode: TemplateCompanyCode = CompanyCode(this@CompanyActivity)


                            companyCode.show()
                            //set adapter into listStudent
                        }
                    }
                    "HOSE" -> stockCompanies!!.clear()
                }
            }
        })
    }


}
