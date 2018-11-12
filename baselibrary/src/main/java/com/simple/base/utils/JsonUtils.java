package com.simple.base.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaolong on 2018/1/26.
 */

public class JsonUtils {
    private static final String TAG = "JsonUtils";

    /**
     * 获取JsonObject
     */
    public static String listToJson(Object obj) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonString = gson.toJson(obj); //转化成json串
        return jsonString;
    }


    /**
     * 转换成实体类对象
     */
    public static <T> T toBean(String jsonString, Class<T> cla) {
        if (isGoodJson(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, cla);
        } else {
            return null;
        }
    }

    /**
     * 根据key从jsonString中获取list
     *
     * @param jsonString
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> getListFromJson(String jsonString, String key, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (jsonString == null || jsonString.length() == 0) {
            return list;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) {
                String json2 = jsonObject.getString(key);
                if (isArray(json2)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    list = toList(jsonArray, clazz);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 转化成List<Object>
     * List<Person> list = toList("", Person.class);
     */
    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        if (!isGoodJson(jsonString)) return null;
        ArrayList<T> list = new ArrayList<>();
        if (isArray(jsonString)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                return toList(jsonArray, clazz);
            } catch (Exception e) {
                e.printStackTrace();
                return list;
            }
        }
        return list;
    }

    /**
     * 将jsonArray转List
     *
     * @param jsonArray
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        Gson gson = IntegerDefault0Adapter.buildGson();
        for (int i = 0; i < jsonArray.length(); i++) {
            T t = gson.fromJson(jsonArray.optJSONObject(i).toString(), clazz);
            list.add(t);
        }
        return list;
    }

    /**
     * List<Map<String, Object>>
     */
    public static List<Map<String, Object>> toMapList(String jsonString) {
        if (isGoodJson(jsonString)) {
            Gson gson = new Gson();
            List<Map<String, Object>> list = gson.fromJson(jsonString, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    public static Map<String, Object> toMap(String jsonString) {
        if (isGoodJson(jsonString)) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<HashMap<String, Object>>() {
            }.getType();
            return gson.fromJson(jsonString, type);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * 判断一个json字符串是否为数组
     */
    public static boolean isArray(String jsonString) {
        if (!isGoodJson(jsonString)) return false;
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                Object json = new JSONTokener(jsonString).nextValue();
                if (json instanceof JSONArray) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    public static boolean isGoodJson(String jsonString) {
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JsonElement jsonElement = new JsonParser().parse(jsonString);
                return (jsonElement.isJsonObject() || jsonElement.isJsonArray() || jsonElement.isJsonNull());
            } catch (JsonParseException e) {
                Log.d(TAG, "bad json: " + jsonString);
                return false;
            }
        }
        return false;
    }


    /**
     * 依据json字符串返回Map对象
     */
    public static Map<String, Object> toMapObj(String jsonString) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        Map<String, Object> map = gson.fromJson(jsonString, type);
        return map;
    }

    /**
     * 忽略某个字段的Gson
     */
    public static Gson getSkipFieldGson(final String field) {
        Gson gson = new GsonBuilder().setExclusionStrategies(
                new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        //过滤掉字段名包含"id","address"的字段
                        return f.getName().equals(field);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        // 过滤掉 类名包含 Bean的类
                        return false;
                    }
                }).create();
        return gson;
    }


}
