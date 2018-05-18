package io.eberlein.insane.bluepwn;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.annotation.TypeConverter;

@TypeConverter
public class LocationTypeConverter extends com.raizlabs.android.dbflow.converter.TypeConverter<String, android.location.Location> {
    @Override
    public String getDBValue(android.location.Location model) {
        return JSON.toJSONString(model);
    }

    @Override
    public android.location.Location getModelValue(String data) {
        return JSON.parseObject(data, android.location.Location.class);
    }
}