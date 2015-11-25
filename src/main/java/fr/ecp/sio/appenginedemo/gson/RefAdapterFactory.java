package fr.ecp.sio.appenginedemo.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.googlecode.objectify.Ref;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Mike on 12/11/2015.
 */
public class RefAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        if (typeToken.getRawType() != Ref.class || !(type instanceof ParameterizedType)) {
            return null;
        }
        Type targetType = ((ParameterizedType) type).getActualTypeArguments()[0];
        TypeAdapter<?> targetAdapter = gson.getAdapter(TypeToken.get(targetType));
        @SuppressWarnings("unchecked")
        TypeAdapter<T> refAdapter = (TypeAdapter<T>) getRefAdapter(targetAdapter);
        return refAdapter;
    }

    private static <TTarget> TypeAdapter<Ref<TTarget>> getRefAdapter(final TypeAdapter<TTarget> targetAdapter) {
        return new TypeAdapter<Ref<TTarget>>() {
            @Override
            public void write(JsonWriter jsonWriter, Ref<TTarget> key) throws IOException {
                if (key == null) {
                    jsonWriter.nullValue();
                } else {
                    targetAdapter.write(jsonWriter, key.getValue());
                }
            }

            @Override
            public Ref<TTarget> read(JsonReader jsonReader) throws IOException {
                final TTarget target = targetAdapter.read(jsonReader);
                if (target == null) return null;
                return new Ref<TTarget>() {

                    @Override
                    public TTarget get() {
                        return target;
                    }

                    @Override
                    public boolean isLoaded() {
                        return true;
                    }

                };
            }
        };
    }

}
