package com.goumkm.yoga.go_umkm.adapter;
import java.text.*;

public class NumberControl
{

    public String getNumberFormat(double nominal ){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator('.');
        formatRp.setGroupingSeparator(',');
        kursIndonesia.setMaximumFractionDigits(0);
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(nominal);
    }

    public String getNumberFormatNoCurrency(double nominal,int fractionDigit){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        kursIndonesia.setDecimalSeparatorAlwaysShown(false);

        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator('.');
        formatRp.setGroupingSeparator(',');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        kursIndonesia.setMaximumFractionDigits(fractionDigit);

        return kursIndonesia.format(nominal);
    }



}

