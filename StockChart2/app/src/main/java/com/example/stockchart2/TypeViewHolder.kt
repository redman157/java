package com.example.stockchart2

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

open class TypeViewHolder(view: View, type: Int) : RecyclerView.ViewHolder(view) {
    private var _Type : Int?= null
    private var type :Type?= null

    init {
        this._Type = type
    }

    open fun getType(): Int? {
        _Type?.let {
            type!!.compareType(it)
        }
        return _Type
    }
}
class CompanyCodeHolder(view: View, type: Int): TypeViewHolder(view, type) {
    public var code : TextView? = null
    public var name : TextView? = null
    public var price : TextView? = null
    public var status : TextView? = null
    public var viewLine : View? = null
    init {
        viewLine = view.findViewById(R.id.viewItemCompanycode)
        code = view.findViewById(R.id.txtItemCompanyCode)
        name = view.findViewById(R.id.txtItemCompanyName)
        price = view.findViewById(R.id.txtItemCompanyPrice)
        status = view.findViewById(R.id.txtItemCompanyStatus)
    }

    override fun getType(): Int? {
        return 0
    }
}