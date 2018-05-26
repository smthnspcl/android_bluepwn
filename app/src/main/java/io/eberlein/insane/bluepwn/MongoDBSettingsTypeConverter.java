package io.eberlein.insane.bluepwn;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.converter.TypeConverter;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MongoDBSettingsTypeConverter extends TypeConverter<MongoDBSettings, String>{
    @Override
    public MongoDBSettings getDBValue(String model) {
        return JSON.parseObject(model, MongoDBSettings.class);
    }

    @Override
    public String getModelValue(MongoDBSettings data) {
        return JSON.toJSONString(data);
    }
}
