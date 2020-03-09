package com.example.stockchart2;

public interface CompanyCodeBuilder {
    CompanyCodeBuilder price(double price);
    CompanyCodeBuilder status(String status);
    CompanyCodeBuilder code(String code);
    CompanyCodeBuilder name(String name);

    CompanyCode build();

}
