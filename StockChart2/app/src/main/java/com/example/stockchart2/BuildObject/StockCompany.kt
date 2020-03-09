package com.example.stockchart2.BuildObject

class StockCompany private constructor(
    public val price: Double?,
    public val code: String?,
    public val name: String?,
    public val status: String?)
    {
        data class Builder
            (
            var code: String? = null,
            var name: String? = null,
            var status: String? = null,
            var price: Double? = null) {

                fun price(price: Double) = apply { this.price = price }
                fun code(code: String) = apply { this.code = code }
                fun name(name: String) = apply { this.name = name }
                fun status(status: String) = apply { this.status = status }
                fun build() = StockCompany(price, code, name, status)
        }
    }