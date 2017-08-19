package com.example.min.googlemaptest;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MIN on 2017-08-14.
 */

public class DataParser {

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
        }//try
        catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return routes;
    }

    public List<List<String>> parse_DT(JSONObject jObject)
    {
        JSONArray jRows;
        JSONArray jElements;

        List<List<String>> data = new ArrayList<>();
        List <String> iDistance = new ArrayList<>();
        List <String> iDuration = new ArrayList<>();
        //elements 는 여러개고
        // distance,duration 조합이 여러개.
        // e
        // distance.get("text") for문 엘리먼츠 사이즈만큼.
        Log.d("DisDur","1");
        try {
            Log.d("DisDur","2");
            jRows = jObject.getJSONArray("rows");
            for(int i=0;i<jRows.length();i++)
            {

                jElements = ((JSONObject)jRows.get(i)).getJSONArray("elements");
                Log.d("DisDur",jElements.get(0).toString());
                for(int j=0;j<jElements.length();j++)
                {
                    Log.d("DisDur","4");
                    JSONObject jDis = ((JSONObject)jElements.get(i)).getJSONObject("distance");
                    JSONObject jDur = ((JSONObject)jElements.get(i)).getJSONObject("duration");

                    Log.d("DisDur",jDis.get("text").toString());
                    Log.d("DisDur",jDur.get("text").toString());

                    iDistance.add(jDis.get("text").toString());
                    iDuration.add(jDur.get("text").toString());
                }
            }
            Log.d("DisDur","5");
            data.add(iDistance);
            data.add(iDuration);


        } catch (JSONException e) {
            Log.d("DisDur","catch당함");
            e.printStackTrace();
        }
        for(int i=0;i<data.get(0).size();i++)
        {
            Log.d("DisDur",data.get(0).get(i)+"\n"+data.get(1).get(i)); //get0 : 거리 , get(1) : 시간
        }
        return data;
//       System.out.println("??");
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
