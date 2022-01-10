package json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lcl100
 * @desc JsonPath工具类
 * @date 2022-01-10
 */
public class JsonPathUtil {

    /**
     * JsonPath配置
     */
    private final static Configuration configuration = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    /**
     * 根据路径表达式读取json中的内容
     *
     * @param json 待读取的json字符串
     * @param path JsonPath路径表达式
     * @return 读取到的结果
     */
    private static Object readPath(String json, String path) {
        Object result = null;
        try {
            result = JsonPath.using(configuration).parse(json).read(path);
        } catch (Exception ignored) {

        }
        return result;
    }

    /**
     * 根据json模板字符串提取json中对应内容然后填充到类中对应映射字段，提取实体类
     *
     * @param json     待提取的json字符串
     * @param jsonPath json模板字符串，存储着实体类属性名与json中对应内容的路径关系
     * @param clazz    待映射的实体类
     * @param <T>      实体类泛型
     * @return 从json字符串中提取了内容后并赋值的实体类对象
     * @throws InstantiationException 创建实例对象异常
     * @throws IllegalAccessException 字段访问权限异常
     */
    public static <T> T getJsonObject(String json, String jsonPath, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        // 提取jsonPath这个json字符串中的所有键值对
        Map<String, Map<String, String>> arrayMap = new HashMap<>();
        Map<String, String> objectMap = new HashMap<>();
        // 判断模板字符串是json对象还是json数组
        jsonPath = jsonPath.trim();
        char firstChar = jsonPath.charAt(0);
        boolean isArray = false;
        if (firstChar == '[') {
            isArray = true;
            // 读取json数组中的所有json对象，每个json对象都是一个Map集合，然后放到List中
            List<Map<String, String>> mapList = (List<Map<String, String>>) readPath(jsonPath, "$");
            for (Map<String, String> map : mapList) {
                arrayMap.put(map.getOrDefault("NAME", null), map);
            }
        } else if (firstChar == '{') {
            isArray = false;
            // 读取所有键值对成一个Map集合
            objectMap = (Map<String, String>) readPath(jsonPath, "$");
        }

        // 获取类的所有字段，依次赋值
        T t = clazz.newInstance();
        Class<?> tClass = t.getClass();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            // 根据字段名提取对应的路径表达式，然后根据路径表达式在json中提取值
            String name = field.getName();
            // 由于模板字符串的不同，提取路径的方式有所不同
            String path = isArray ? (arrayMap.getOrDefault(name, null) != null ? arrayMap.get(name).get("PATH") : null) : objectMap.getOrDefault(name, null);
            // 根据路径读取值
            Object value = readPath(json, path);
            // 如果使用的是带有正则表达式的模板字符串则需要额外处理
            if (isArray) {
                // 进行正则提取
                Matcher matcher = Pattern.compile(arrayMap.getOrDefault(name, null).getOrDefault("REGEXP", null)).matcher(String.valueOf(value));
                if (matcher.find()) {
                    value = matcher.group(1);
                }
            }
            // 为字段赋值
            setField(field, t, value);
        }

        return t;
    }

    /**
     * 根据json模板字符串提取json中对应内容然后填充到类中对应映射字段，提取实体类集合
     *
     * @param json     待提取的json字符串
     * @param jsonPath json模板字符串，存储着实体类属性名与json中对应内容的路径关系
     * @param clazz    待映射的实体类
     * @param <T>      实体类泛型
     * @return 赋值后的实体类集合
     * @throws InstantiationException 创建实例对象异常
     * @throws IllegalAccessException 字段访问权限异常
     */
    public static <T> List<T> getJsonList(String json, String jsonPath, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        Map<String, Map<String, String>> arrayMap = new HashMap<>();
        Map<String, String> objectMap = new HashMap<>();
        // 根据是否有"DATA"来判断模板字符串是什么模式
        boolean isArray = jsonPath.contains("DATA");
        if (isArray) {// 该模式使用了正则表达式
            // 直接是一个数组，包含多个json对象，即包含多个Map集合
            List<Map<String, String>> mapList = (List<Map<String, String>>) readPath(jsonPath, "$.DATA");
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, String> map = mapList.get(i);
                String name = map.get("NAME");
                String path = map.get("PATH");
                if (name != null && !"".equals(name.trim()) && path != null && !"".equals(path.trim())) {
                    arrayMap.put(name, map);
                }
            }
        } else {// 该模式未使用正则表达式
            // 直接是一个Map集合，包括所有的键值对
            objectMap = (Map<String, String>) readPath(jsonPath, "$");
        }

        // 计算ROOT_LIST所表示路径在json字符串中代表的数组的长度
        String rootPath = (String) readPath(jsonPath, "$.ROOT_LIST");
        int length = (int) readPath(json, rootPath + ".length()");
        for (int j = 0; j < length; j++) {
            // 创建实体类对象
            T t = clazz.newInstance();
            Class<?> tClass = t.getClass();
            // 获取所有声明的字段
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                // 字段名
                String fieldName = field.getName();
                // 待赋给字段的值
                Object value;
                if (isArray) {
                    // 过滤掉不需要提取的字段
                    if (arrayMap.getOrDefault(fieldName, null) == null) {
                        continue;
                    }
                    // 从json中读取指定路径的值
                    value = readPath(json, rootPath + "[" + j + "]" + arrayMap.getOrDefault(fieldName, null).get("PATH"));
                    // 需要进行正则处理
                    Matcher matcher = Pattern.compile(arrayMap.getOrDefault(fieldName, null).getOrDefault("REGEXP", null)).matcher(String.valueOf(value));
                    if (matcher.find()) {
                        value = matcher.group(1);
                    }
                } else {
                    // 过滤掉不需要提取的字段
                    if (objectMap.getOrDefault(fieldName, null) == null) {
                        continue;
                    }
                    // 从json中读取指定路径的值
                    value = readPath(json, rootPath + "[" + j + "]" + objectMap.getOrDefault(fieldName, null));
                }
                // 为字段赋值
                setField(field, t, value);
            }
            list.add(t);
        }
        return list;
    }

    /**
     * 根据字段类型为实例对象上的字段进行赋值
     *
     * @param field 待赋值的字段
     * @param t     实例对象
     * @param value 待赋予的值
     * @throws IllegalAccessException 字段访问权限异常
     */
    private static void setField(Field field, Object t, Object value) throws IllegalAccessException {
        String typeName = field.getType().getTypeName();
        field.setAccessible(true);
        if (typeName.endsWith("String")) {
            field.set(t, String.valueOf(value));
        } else if (typeName.endsWith("byte")) {
            byte result = 0;
            try {
                result = Byte.parseByte(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Byte")) {
            Byte result = null;
            try {
                result = Byte.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("short")) {
            short result = 0;
            try {
                result = Short.parseShort(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Short")) {
            Short result = null;
            try {
                result = Short.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("int")) {
            int result = 0;
            try {
                result = Integer.parseInt(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Integer")) {
            Integer result = null;
            try {
                result = Integer.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("long")) {
            long result = 0L;
            try {
                result = Long.parseLong(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Long")) {
            Long result = null;
            try {
                result = Long.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("float")) {
            float result = 0F;
            try {
                result = Float.parseFloat(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Float")) {
            Float result = null;
            try {
                result = Float.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("double")) {
            double result = 0;
            try {
                result = Double.parseDouble(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Double")) {
            Double result = null;
            try {
                result = Double.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("boolean")) {
            boolean result = false;
            try {
                result = Boolean.parseBoolean(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Boolean")) {
            Boolean result = null;
            try {
                result = Boolean.valueOf(value.toString());
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("char")) {
            char result = '\u0000';
            try {
                result = value.toString().charAt(0);
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Character")) {
            Character result = null;
            try {
                result = value.toString().charAt(0);
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else if (typeName.endsWith("Date")) {
            Date result = null;
            // 暂时只能处理时间戳的日期
            try {
                Matcher timestampMatcher = Pattern.compile("1[\\d]{9}").matcher(value.toString());
                if (timestampMatcher.find()) {
                    result = new Date(Long.parseLong(value.toString()));
                }
            } catch (Exception ignored) {

            }
            field.set(t, result);
        } else {
            System.out.println("无法转换的字段类型: " + typeName);
        }
    }
}

/*提取实体类
模板一：
    {
      "category": "$.store.book[0].category",
      "author": "$.store.book[0].author",
      "title": "$.store.book[0].title",
      "isbn": "$.store.book[0].isbn",
      "price": "$.store.book[0].price"
    }
模板二：
    [
      {
        "NAME": "category",
        "PATH": "$.store.book[0].category",
        "REGEXP": "(.*)"
      },
      {
        "NAME": "author",
        "PATH": "$.store.book[0].author",
        "REGEXP": "(.*)"
      },
      {
        "NAME": "title",
        "PATH": "$.store.book[0].title",
        "REGEXP": "(.*)"
      },
      {
        "NAME": "isbn",
        "PATH": "$.store.book[0].isbn",
        "REGEXP": "(.*)"
      },
      {
        "NAME": "price",
        "PATH": "$.store.book[0].price",
        "REGEXP": "(\\d+\\.\\d+)"
      }
    ]
*/

/*提取实体类集合
模板一：
    {
      "ROOT_LIST": "$.store.book",
      "category": ".category",
      "author": ".author",
      "title": ".title",
      "isbn": ".isbn",
      "price": ".price"
    }
模板二：
    {
      "ROOT_LIST": "$.store.book",
      "DATA": [
        {
          "NAME": "category",
          "PATH": ".category",
          "REGEXP": "(.*)"
        },
        {
          "NAME": "author",
          "PATH": ".author",
          "REGEXP": "(.*)"
        },
        {
          "NAME": "title",
          "PATH": ".title",
          "REGEXP": "(.*)"
        },
        {
          "NAME": "isbn",
          "PATH": ".isbn",
          "REGEXP": "(.*)"
        },
        {
          "NAME": "price",
          "PATH": ".price",
          "REGEXP": "(.*)"
        }
      ]
    }
 */

