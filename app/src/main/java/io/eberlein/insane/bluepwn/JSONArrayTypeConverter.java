package io.eberlein.insane.bluepwn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.raizlabs.android.dbflow.annotation.TypeConverter;

@TypeConverter
public class JSONArrayTypeConverter extends com.raizlabs.android.dbflow.converter.TypeConverter<String, JSONArray> {
    @Override
    public JSONArray getModelValue(String data) {
        return JSON.parseArray(data);
    }

    @Override
    public String getDBValue(JSONArray model) {
        return JSON.toJSONString(model);
    }
}
