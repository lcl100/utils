package json;

import org.junit.Test;

import java.util.List;

/**
 * @author lcl100
 * @desc JsonPathUtil类的测试类
 * @create 2022-01-10 21:30
 */
public class JsonPathUtilTest {
    private final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

    @Test
    public void testGetJsonObject() throws InstantiationException, IllegalAccessException {
        // 注意，jsonPath模板表达式中，键名为实体类中的属性名；键值为在json字符串中的完整路径

        // 第一种形式的模板字符串
        String jsonPath1 = "{\n" +
                "  \"category\": \"$.store.book[0].category\",\n" +
                "  \"author\": \"$.store.book[0].author\",\n" +
                "  \"title\": \"$.store.book[0].title\",\n" +
                "  \"isbn\": \"$.store.book[0].isbn\",\n" +
                "  \"price\": \"$.store.book[0].price\"\n" +
                "}";
        Book book1 = JsonPathUtil.getJsonObject(json, jsonPath1, Book.class);
        System.out.println(book1);

        // 第二种形式的模板字符串
        String jsonPath2 = "[\n" +
                "  {\n" +
                "    \"NAME\": \"category\",\n" +
                "    \"PATH\": \"$.store.book[0].category\",\n" +
                "    \"REGEXP\": \"(.*)\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"NAME\": \"author\",\n" +
                "    \"PATH\": \"$.store.book[0].author\",\n" +
                "    \"REGEXP\": \"(.*)\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"NAME\": \"title\",\n" +
                "    \"PATH\": \"$.store.book[0].title\",\n" +
                "    \"REGEXP\": \"(.*)\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"NAME\": \"isbn\",\n" +
                "    \"PATH\": \"$.store.book[0].isbn\",\n" +
                "    \"REGEXP\": \"(.*)\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"NAME\": \"price\",\n" +
                "    \"PATH\": \"$.store.book[0].price\",\n" +
                "    \"REGEXP\": \"(\\\\d+\\\\.\\\\d+)\"\n" +
                "  }\n" +
                "]";
        Book book2 = JsonPathUtil.getJsonObject(json, jsonPath1, Book.class);
        System.out.println(book2);
    }

    @Test
    public void testGetJsonList() throws InstantiationException, IllegalAccessException {
        // 注意，ROOT_LIST为必选字段，表示列表的根路径
        // 注意，jsonPath模板表达式中，键名为实体类中的属性名；键值为在json字符串中的相对路径（相对列表的根路径而言）

        // 第一种形式的模板字符串
        String jsonPath1 = "{\n" +
                "  \"ROOT_LIST\": \"$.store.book\",\n" +
                "  \"category\": \".category\",\n" +
                "  \"author\": \".author\",\n" +
                "  \"title\": \".title\",\n" +
                "  \"isbn\": \".isbn\",\n" +
                "  \"price\": \".price\"\n" +
                "}";
        List<Book> bookList1 = JsonPathUtil.getJsonList(json, jsonPath1, Book.class);
        for (Book book : bookList1) {
            System.out.println(book);
        }

        // 第二种形式的模板字符串
        String jsonPath2 = "{\n" +
                "  \"ROOT_LIST\": \"$.store.book\",\n" +
                "  \"DATA\": [\n" +
                "    {\n" +
                "      \"NAME\": \"category\",\n" +
                "      \"PATH\": \".category\",\n" +
                "      \"REGEXP\": \"(.*)\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"NAME\": \"author\",\n" +
                "      \"PATH\": \".author\",\n" +
                "      \"REGEXP\": \"(.*)\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"NAME\": \"title\",\n" +
                "      \"PATH\": \".title\",\n" +
                "      \"REGEXP\": \"(.*)\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"NAME\": \"isbn\",\n" +
                "      \"PATH\": \".isbn\",\n" +
                "      \"REGEXP\": \"(.*)\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"NAME\": \"price\",\n" +
                "      \"PATH\": \".price\",\n" +
                "      \"REGEXP\": \"(\\\\d+\\\\.\\\\d+)\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        List<Book> bookList2 = JsonPathUtil.getJsonList(json, jsonPath2, Book.class);
        for (Book book : bookList2) {
            System.out.println(book);
        }
    }
}


