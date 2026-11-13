package cn.edu.sicnu.cs.stu.baixinyuan.android_qz;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import cn.edu.sicnu.cs.stu.baixinyuan.android_qz.model.Book;

/**
 * 第 2 个 Activity：显示被选中书籍的完整信息。
 */
public class DetailActivity extends AppCompatActivity {

    /** 通过 Intent 传递书籍对象的键。 */
    public static final String EXTRA_BOOK = "cn.edu.sicnu.cs.stu.baixinyuan.android_qz.extra.BOOK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        applySystemBarInsets(findViewById(R.id.root));

        Book book = readBookFromIntent();
        if (book == null) {
            // 没有拿到书籍数据时直接关闭，避免空指针
            finish();
            return;
        }

        bindViews(book);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.button_back).setOnClickListener(v -> finish());
    }

    /**
     * 取出被选中的书籍。Android 13 起推荐使用带 Class 参数的重载以规避类型转换风险。
     */
    @SuppressWarnings("deprecation")
    private Book readBookFromIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(EXTRA_BOOK, Book.class);
        }
        return (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
    }

    private void bindViews(@NonNull Book book) {
        setText(R.id.text_detail_title, book.getTitle());
        setText(R.id.text_detail_author, book.getAuthor());
        setText(R.id.text_detail_publisher, book.getPublisher());
        setText(R.id.text_detail_price, getString(R.string.price_format, book.getPriceValueText()));
        setText(R.id.text_detail_year, getString(R.string.year_format, book.getYear()));
        setText(R.id.text_detail_category, book.getCategory());
        setText(R.id.text_detail_isbn, book.getIsbn());
        setText(R.id.text_detail_summary, book.getSummary());
    }

    private void setText(int viewId, CharSequence text) {
        TextView textView = findViewById(viewId);
        textView.setText(text);
    }

    /**
     * 适配 Android 15 及以上的强制边到边显示。
     */
    private void applySystemBarInsets(@NonNull View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
