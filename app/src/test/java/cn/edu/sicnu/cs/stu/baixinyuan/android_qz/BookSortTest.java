package cn.edu.sicnu.cs.stu.baixinyuan.android_qz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sicnu.cs.stu.baixinyuan.android_qz.model.Book;

/**
 * 书籍模型与排序、分类筛选逻辑的单元测试。
 */
public class BookSortTest {

    private static Book book(String title, String category, double price) {
        return new Book(title, "作者", "出版社", price, 2020, category, "ISBN", "简介");
    }

    private static List<Book> sampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(book("算法导论", "计算机", 128.00));
        books.add(book("代码整洁之道", "计算机", 59.00));
        books.add(book("活着", "文学", 45.00));
        books.add(book("围城", "文学", 39.00));
        books.add(book("时间简史", "科普", 45.00));
        return books;
    }

    @Test
    public void sortByTitleAscending_usesChinesePinyinOrder() {
        List<Book> books = sampleBooks();

        books.sort(Book.byTitleAscending());

        // 拼音顺序：代(dai) < 活(huo) < 时(shi) < 算(suan) < 围(wei)
        assertEquals("代码整洁之道", books.get(0).getTitle());
        assertEquals("活着", books.get(1).getTitle());
        assertEquals("时间简史", books.get(2).getTitle());
        assertEquals("算法导论", books.get(3).getTitle());
        assertEquals("围城", books.get(4).getTitle());
    }

    @Test
    public void sortByPriceAscending_ordersFromLowToHigh() {
        List<Book> books = sampleBooks();

        books.sort(Book.byPriceAscending());

        assertEquals(39.00, books.get(0).getPrice(), 0.0001);
        assertEquals(128.00, books.get(books.size() - 1).getPrice(), 0.0001);
        for (int i = 1; i < books.size(); i++) {
            assertTrue("价格应从小到大排列", books.get(i - 1).getPrice() <= books.get(i).getPrice());
        }
    }

    @Test
    public void sortByPriceDescending_ordersFromHighToLow() {
        List<Book> books = sampleBooks();

        books.sort(Book.byPriceDescending());

        assertEquals(128.00, books.get(0).getPrice(), 0.0001);
        assertEquals(39.00, books.get(books.size() - 1).getPrice(), 0.0001);
        for (int i = 1; i < books.size(); i++) {
            assertTrue("价格应从大到小排列", books.get(i - 1).getPrice() >= books.get(i).getPrice());
        }
    }

    @Test
    public void filterByCategory_keepsOnlyMatchingBooks() {
        List<Book> books = sampleBooks();

        List<Book> computers = Book.filterByCategory(books, "计算机");
        List<Book> literatures = Book.filterByCategory(books, "文学");

        assertEquals(2, computers.size());
        // 筛选保持原有顺序：算法导论在前，代码整洁之道在后
        assertEquals("算法导论", computers.get(0).getTitle());
        assertEquals("代码整洁之道", computers.get(1).getTitle());
        assertEquals(2, literatures.size());
        assertEquals("活着", literatures.get(0).getTitle());
    }

    @Test
    public void filterByCategory_withNullCategory_returnsAll() {
        List<Book> books = sampleBooks();

        List<Book> all = Book.filterByCategory(books, null);

        assertEquals(books.size(), all.size());
    }

    @Test
    public void priceValueText_alwaysHasTwoDecimals() {
        assertEquals("59.00", book("A", "计算机", 59.00).getPriceValueText());
        assertEquals("108.50", book("B", "计算机", 108.5).getPriceValueText());
    }
}
