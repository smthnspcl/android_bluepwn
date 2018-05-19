package io.eberlein.insane.bluepwn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.raizlabs.android.dbflow.annotation.TypeConverter;

@TypeConverter
public class JSONObjectTypeConverter extends com.raizlabs.android.dbflow.converter.TypeConverter<String, JSONObject>{
    @Override
    public String getDBValue(JSONObject model) {
        return JSON.toJSONString(model);
    }

    @Override
    public JSONObject getModelValue(String data) {
        return JSON.parseObject(data, JSONObject.class);
    }
}
