package io.eberlein.insane.bluepwn;

import android.os.ParcelUuid;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.UUID;

public class ParcelUuidTypeConverter extends TypeConverter<String, ParcelUuid> {
    @Override
    public ParcelUuid getModelValue(String data) {
        return new ParcelUuid(UUID.fromString(data));
    }

    @Override
    public String getDBValue(ParcelUuid model) {
        return model.getUuid().toString();
    }
}
