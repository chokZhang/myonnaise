package com.mengyuan1998.finger_dancing.Utilities;



import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.VedioItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<BaseItem> parseJSONWithJSONObject(String jsondata) throws Exception {
        JSONArray jsonArray = new JSONArray(jsondata);
        List<BaseItem> items = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            items.add(new VedioItem(jsonObject));
        }

        return items;
    }
}
