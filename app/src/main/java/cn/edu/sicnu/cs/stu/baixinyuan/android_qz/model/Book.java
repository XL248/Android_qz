package cn.edu.sicnu.cs.stu.baixinyuan.android_qz.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 书籍实体类。
 *
 * <p>属性包含书名、作者、出版社、价格，以及出版年份、分类、ISBN、内容简介等扩展信息。
 * 实现 {@link Serializable} 以便通过 Intent 在两个 Activity 之间传递；
 * 同时提供按书名（中文按拼音）与按价格排序的静态比较器。</p>
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 中文书名排序使用拼音顺序。 */
    private static final Collator CHINESE_COLLATOR = Collator.getInstance(Locale.CHINA);

    private final String title;
    private final String author;
    private final String publisher;
    private final double price;
    private final int year;
    private final String category;
    private final String isbn;
    private final String summary;

    public Book(String title, String author, String publisher, double price,
                int year, String category, String isbn, String summary) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.year = year;
        this.category = category;
        this.isbn = isbn;
        this.summary = summary;
    }

    /**
     * 按书名升序排序，中文按拼音顺序比较。
     */
    public static Comparator<Book> byTitleAscending() {
        return (left, right) -> {
            int result = CHINESE_COLLATOR.compare(left.title, right.title);
            return result != 0 ? result : left.author.compareTo(right.author);
        };
    }

    /**
     * 按价格从低到高排序（价格相同时保持书名顺序，结果稳定）。
     */
    public static Comparator<Book> byPriceAscending() {
        return Comparator.comparingDouble(Book::getPrice).thenComparing(byTitleAscending());
    }

    /**
     * 按价格从高到低排序。
     */
    public static Comparator<Book> byPriceDescending() {
        return byPriceAscending().reversed();
    }

    /**
     * 按分类筛选书籍；category 为 null 表示不筛选（返回全部）。
     */
    public static List<Book> filterByCategory(List<Book> books, String category) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (category == null || category.equals(book.getCategory())) {
                result.add(book);
            }
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public double getPrice() {
        return price;
    }

    public int getYear() {
        return year;
    }

    public String getCategory() {
        return category;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSummary() {
        return summary;
    }

    /**
     * 格式化后的价格文本，固定两位小数，例如 59.00。
     */
    public String getPriceValueText() {
        return String.format(Locale.CHINA, "%.2f", price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book other = (Book) o;
        return Double.compare(price, other.price) == 0
                && year == other.year
                && title.equals(other.title)
                && author.equals(other.author)
                && publisher.equals(other.publisher)
                && category.equals(other.category)
                && isbn.equals(other.isbn)
                && summary.equals(other.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, price, year, category, isbn, summary);
    }
}
