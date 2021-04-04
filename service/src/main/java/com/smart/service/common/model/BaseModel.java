package com.smart.service.common.model;

import com.google.gson.Gson;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenjunlong
 */
public abstract class BaseModel {

    /**
     * ******************* json serializable *********************
     */
    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static <T> T parseFromJson(String value, Class clazz) {
        return (T) gson.fromJson(value, clazz);
    }



    /**
     * ******************* proto stuff serializable *********************
     */
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    public <T> byte[] toPb() {
        T object = (T) this;
        Class<T> clazz = (Class<T>) object.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    public static <T> T parseFromPb(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T object = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, object, schema);
        return object;
    }

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            // 这个schema通过RuntimeSchema进行懒创建并缓存
            // 所以可以一直调用RuntimeSchema.getSchema(), 这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

}
