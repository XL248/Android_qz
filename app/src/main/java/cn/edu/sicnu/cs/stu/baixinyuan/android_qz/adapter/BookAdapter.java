package cn.edu.sicnu.cs.stu.baixinyuan.android_qz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import cn.edu.sicnu.cs.stu.baixinyuan.android_qz.R;
import cn.edu.sicnu.cs.stu.baixinyuan.android_qz.model.Book;

/**
 * 书籍列表适配器：把当前要展示的书籍集合绑定到 RecyclerView 上，并把点击事件回调给 Activity。
 *
 * <p>继承 {@link ListAdapter}，切换排序方式或分类时由 {@link DiffUtil} 计算差量，
 * 只刷新真正变化的条目，切换过程不会闪烁。</p>
 */
public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {

    private static final DiffUtil.ItemCallback<Book> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Book>() {
                @Override
                public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getAuthor().equals(newItem.getAuthor());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                    return oldItem.equals(newItem);
                }
            };

    /**
     * 列表项点击回调，由 Activity 实现（点击后启动第 2 个 Activity）。
     */
    public interface OnBookClickListener {
        void onBookClick(@NonNull Book book);
    }

    private final OnBookClickListener listener;

    public BookAdapter(@NonNull OnBookClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = getItem(position);
        Context context = holder.itemView.getContext();

        holder.textTitle.setText(book.getTitle());
        holder.textMeta.setText(context.getString(
                R.string.book_meta_format,
                book.getAuthor(),
                book.getPublisher(),
                book.getCategory()));
        holder.textPrice.setText(context.getString(
                R.string.price_format, book.getPriceValueText()));

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        private final TextView textTitle;
        private final TextView textMeta;
        private final TextView textPrice;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_book_title);
            textMeta = itemView.findViewById(R.id.text_book_meta);
            textPrice = itemView.findViewById(R.id.text_book_price);
        }
    }
}
