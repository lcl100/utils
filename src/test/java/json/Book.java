package json;

/**
 * @author lcl100
 * @create 2022-01-10 21:36
 */
class Book {
    private String category;
    private String author;
    private String title;
    private String isbn;
    private float price;


    public Book() {
    }

    public Book(String category, String author, String title, String isbn, float price) {
        this.category = category;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", isbin='" + isbn + '\'' +
                ", price=" + price +
                '}';
    }
}