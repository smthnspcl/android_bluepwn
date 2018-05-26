package io.eberlein.insane.bluepwn;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.converter.TypeConverter;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MongoDBSettingsTypeConverter extends TypeConverter<String, MongoDBSettings>{
    @Override
    public MongoDBSettings getModelValue(String data) {
        return JSON.parseObject(data, MongoDBSettings.class);
    }

    @Override
    public String getDBValue(MongoDBSettings model) {
        return JSON.toJSONString(model);
    }
}
