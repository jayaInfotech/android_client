package com.bestspa.spa.client.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJSONParser {
    public List<HashMap<String, String>> parse(JSONObject jObject) {
        Double lat = Double.valueOf(0.0d);
        Double lng = Double.valueOf(0.0d);
        HashMap<String, String> hm = new HashMap();
        List<HashMap<String, String>> list = new ArrayList();
        try {
            lat = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat");
            lng = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            System.out.println("");
            e2.printStackTrace();
        }
        hm.put("lat", Double.toString(lat.doubleValue()));
        hm.put("lng", Double.toString(lng.doubleValue()));
        list.add(hm);
        return list;
    }
}
