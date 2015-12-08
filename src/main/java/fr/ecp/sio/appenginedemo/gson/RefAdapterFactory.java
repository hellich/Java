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
 * This AdapterFactory will help Gson handle the Ref<> fields in our model classes.
 * The Ref<> class represents a reference to an entity in the datastore (lazy loading).
 * But in the JSON responses, a Ref<> should just be replaced by the actual object it is pointing at.
 * As Ref<T> is a generic class, the actual processing depends on the type T of the entity.
 * That's why we define a Factory, that can create a specific Adapter for each entity type.
 */
public class RefAdapterFactory implements TypeAdapterFactory {

    // This generic method is called by Gson for every different class it tries to convert to JSON.
    // All registered factories are sent all the encountered types once!
    // A factory must return null if it cannot handle a type.
    // A factory must return a specific adapter to handle a supported type.
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        // Inspect the typeToken submitted to our factory (this represents a simple class, like User, or a generic type like List<User>)
        Type type = typeToken.getType();
        if (typeToken.getRawType() != Ref.class || !(type instanceof ParameterizedType)) {
            // The type if not a Ref<>, stop now
            return null;
        }
        // A Ref<> of what? We need the type of the target, e.g. User.class
        Type targetType = ((ParameterizedType) type).getActualTypeArguments()[0];
        // Now that we know the type of the target, get the standard Gson adapter for it
        TypeAdapter<?> targetAdapter = gson.getAdapter(TypeToken.get(targetType));
        // Create and return an adapter for Ref<T> using the adapter of T
        @SuppressWarnings("unchecked")
        TypeAdapter<T> refAdapter = (TypeAdapter<T>) getRefAdapter(targetAdapter);
        return refAdapter;
    }

    // This method creates and returns an anonymous TypeAdapter for Ref<T>, given the TypeAdapter of T
    private static <TTarget> TypeAdapter<Ref<TTarget>> getRefAdapter(final TypeAdapter<TTarget> targetAdapter) {
        // A TypeAdapter basically ony have to do two things: write an object as JSON, and the other way
        return new TypeAdapter<Ref<TTarget>>() {

            // write() is called when the adapter must write an object (Ref<TTarget>) to JSON
            @Override
            public void write(JsonWriter jsonWriter, Ref<TTarget> key) throws IOException {
                if (key == null) {
                    // The Ref<> is null, just put a "null" element in the JSON tree
                    jsonWriter.nullValue();
                } else {
                    // Get the actual target of the Ref<> with getValue(), and use the provided target adapter to write it
                    targetAdapter.write(jsonWriter, key.getValue());
                }
            }

            // read() is called when the adapter have to parse a JSON and transform it into a Ref<> object
            @Override
            public Ref<TTarget> read(JsonReader jsonReader) throws IOException {
                // Let our targetAdapter read the object directly
                final TTarget target = targetAdapter.read(jsonReader);
                if (target == null) {
                    return null;
                } else {
                    // Encapsulate the result inside a new Ref<>
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
            }
        };
    }

}
