package com.goumkm.yoga.go_umkm.adapter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class valueComparator implements Comparator<String> {
    HashMap<String, Double> map = new HashMap<String, Double>();

    public valueComparator(HashMap<String, Double> map){
        this.map.putAll(map);
    }

    @Override
    public int compare(String s1, String s2) {
        if(map.get(s1) >= map.get(s2)){
            return -1;
        }else{
            return 1;
        }
    }
}
