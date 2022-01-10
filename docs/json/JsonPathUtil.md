# JsonPathUtil工具类

## 静态方法说明

- `T getJsonObject(String json, String jsonPath, Class<T> clazz)`：根据jsonPath提取json字符串中内容返回一个封装了数据的实体类。
- `List<T> getJsonList(String json, String jsonPath, Class<T> clazz)`：根据jsonPath提取json字符串中内容返回一个封装了数据的实体类集合。

## 方法详细说明

### `getJsonObject`

#### 方法：

- `T getJsonObject(String json, String jsonPath, Class<T> clazz)`

#### 参数：

- `json`：待解析的json字符串。
- `jsonPath`：jsonPath模板字符串，也是一个json格式的字符串，支持两种形式，是解析json字符串的路径表达式。
- `clazz`：待封装数据的实体类。

#### 返回值：

- `T`：封装了实体类的实例对象

#### 说明：

`jsonPath`支持下面两种形式的模板字符串：

- 第一种格式：语法简单，但不支持正则表达式。 语法如下：

```json
{
  "实体类属性名": "jsonPath路径",
  "实体类属性名": "jsonPath路径",
  ...
}
```

示例：

```json
{
  "category": "$.store.book[0].category",
  "author": "$.store.book[0].author",
  "title": "$.store.book[0].title",
  "isbn": "$.store.book[0].isbn",
  "price": "$.store.book[0].price"
}
```

- 第二种格式：语法较为复杂，支持正则表达式提取数据（注意，必须分组，会提取第一个分组的内容）。

语法如下：

```json
[
  {
    "NAME": "实体类属性名",
    "PATH": "jsonPath路径",
    "REGEXP": "正则表达式"
  },
  {
    "NAME": "实体类属性名",
    "PATH": "jsonPath路径",
    "REGEXP": "正则表达式"
  },
  ...
]
```

示例如下：

```json
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
```

更多可参考`src/test/java/json/JsonPathUtilTest.java`。

### `getJsonList`

#### 方法：

- `List<T> getJsonList(String json, String jsonPath, Class<T> clazz)`

#### 参数：

- `json`：待解析的json字符串。
- `jsonPath`：jsonPath模板字符串，也是一个json格式的字符串，支持两种形式，是解析json字符串的路径表达式。
- `clazz`：待封装数据的实体类。

#### 返回值：

- `List<T>`：封装了实体类的实例对象的List集合。

#### 说明：

`jsonPath`支持下面两种形式的模板字符串：

- 第一种格式：语法简单，但不支持正则表达式。

语法如下（其中`ROOT_LIST`为必选字段。）：

```json
{
  "ROOT_LIST": "列表根路径",
  "实体类属性名": "子路径",
  "实体类属性名": "子路径",
  ...
}
```

示例如下：

```json
{
  "ROOT_LIST": "$.store.book",
  "category": ".category",
  "author": ".author",
  "title": ".title",
  "isbn": ".isbn",
  "price": ".price"
}
```

- 第二种格式：语法较为复杂，支持正则表达式提取数据（注意，必须分组，会提取第一个分组的内容）。

语法如下：

```json
{
  "ROOT_LIST": "列表根路径",
  "DATA": [
    {
      "NAME": "实体类字段名",
      "PATH": "子路径",
      "REGEXP": "正则表达式"
    },
    {
      "NAME": "实体类字段名",
      "PATH": "子路径",
      "REGEXP": "正则表达式"
    },
    ...
  ]
}
```

示例如下：

```json
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
```

更多可参考`src/test/java/json/JsonPathUtilTest.java`。
