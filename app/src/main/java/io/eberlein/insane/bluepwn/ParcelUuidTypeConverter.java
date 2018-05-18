package io.eberlein.insane.bluepwn;

import android.os.ParcelUuid;

import com.alibaba.fastjson.JSON;
import com.raizlabs.android.dbflow.converter.TypeConverter;

public class ParcelUuidTypeConverter extends TypeConverter<String, ParcelUuid> {
    @Override
    public ParcelUuid getModelValue(String data) {
        return JSON.parseObject(data, ParcelUuid.class);
    }

    @Override
    public String getDBValue(ParcelUuid model) {
        return JSON.toJSONString(model);
    }
}
