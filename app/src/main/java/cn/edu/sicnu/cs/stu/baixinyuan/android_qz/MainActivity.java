package cn.edu.sicnu.cs.stu.baixinyuan.android_qz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.sicnu.cs.stu.baixinyuan.android_qz.adapter.BookAdapter;
import cn.edu.sicnu.cs.stu.baixinyuan.android_qz.model.Book;

/**
 * 第 1 个 Activity：展示书籍列表。
 *
 * <p>在代码中创建多个书籍对象放入数组，默认按书名首字母（中文按拼音）排序，
 * 也可以通过顶部的排序标签切换为按价格从低到高 / 从高到低；
 * 分类标签可以把列表过滤成某一个类别。单击某一本书籍会启动第 2 个 Activity。</p>
 */
public class MainActivity extends AppCompatActivity {

    /** 排序方式。 */
    private enum SortOrder {
        TITLE,
        PRICE_ASCENDING,
        PRICE_DESCENDING
    }

    /** 全部书籍（顺序即创建顺序，展示前统一排序）。 */
    private final List<Book> allBooks = new ArrayList<>();

    /** 分类标签对应的分类名，key 为 Chip 的 id。 */
    private final Map<Integer, String> categoryByChipId = new HashMap<>();

    private BookAdapter adapter;
    private RecyclerView recyclerView;
    private TextView textSubtitle;

    private SortOrder sortOrder = SortOrder.TITLE;
    /** 当前选中的分类，null 表示"全部"。 */
    private String selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        applySystemBarInsets(findViewById(R.id.root));

        // 第 1 个 Activity 的标题：姓名 + 学号
        setTitle(R.string.screen_title);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.screen_title);

        textSubtitle = findViewById(R.id.text_subtitle);
        recyclerView = findViewById(R.id.recycler_books);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(this::openDetail);
        recyclerView.setAdapter(adapter);

        // 1) 在代码中新建 50 个书籍对象并放入数组
        createBooks();

        // 2) 排序标签与分类标签
        setupSortChips();
        setupCategoryChips();

        // 3) 首次展示：默认按书名首字母排序、显示全部分类
        applyFilterAndSort();
    }

    /**
     * 单击列表项：启动第 2 个 Activity，并把被选中的书籍传递过去。
     */
    private void openDetail(@NonNull Book book) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BOOK, book);
        startActivity(intent);
    }

    /**
     * 排序标签：首字母 / 价格从低到高 / 价格从高到低。
     */
    private void setupSortChips() {
        ChipGroup chipsSort = findViewById(R.id.chips_sort);
        chipsSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_sort_price_ascending) {
                sortOrder = SortOrder.PRICE_ASCENDING;
            } else if (checkedId == R.id.chip_sort_price_descending) {
                sortOrder = SortOrder.PRICE_DESCENDING;
            } else {
                sortOrder = SortOrder.TITLE;
            }
            applyFilterAndSort();
        });
    }

    /**
     * 分类标签：根据数据中出现的分类动态生成，"全部"排在第一个。
     */
    private void setupCategoryChips() {
        ChipGroup chipsCategory = findViewById(R.id.chips_category);

        Chip allChip = createCategoryChip(getString(R.string.category_all));
        allChip.setId(View.generateViewId());
        allChip.setChecked(true);
        categoryByChipId.put(allChip.getId(), null);
        chipsCategory.addView(allChip);

        for (String category : collectCategories()) {
            Chip chip = createCategoryChip(category);
            chip.setId(View.generateViewId());
            categoryByChipId.put(chip.getId(), category);
            chipsCategory.addView(chip);
        }

        chipsCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            selectedCategory = categoryByChipId.get(checkedIds.get(0));
            applyFilterAndSort();
        });
    }

    @NonNull
    private Chip createCategoryChip(@NonNull CharSequence text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setCheckedIconVisible(false);
        chip.setChipBackgroundColor(getColorStateList(R.color.selector_chip_background));
        chip.setTextColor(getColorStateList(R.color.selector_chip_text));
        return chip;
    }

    /**
     * 按数据中首次出现的顺序收集分类名。
     */
    @NonNull
    private List<String> collectCategories() {
        List<String> categories = new ArrayList<>();
        for (Book book : allBooks) {
            if (!categories.contains(book.getCategory())) {
                categories.add(book.getCategory());
            }
        }
        return categories;
    }

    /**
     * 按当前排序方式与分类过滤出要展示的数据。
     */
    private void applyFilterAndSort() {
        List<Book> visible = Book.filterByCategory(allBooks, selectedCategory);
        visible.sort(comparatorFor(sortOrder));

        int count = visible.size();
        textSubtitle.setText(getString(R.string.list_subtitle, count, getString(labelFor(sortOrder))));
        adapter.submitList(visible, () -> recyclerView.scrollToPosition(0));
    }

    @NonNull
    private Comparator<Book> comparatorFor(@NonNull SortOrder order) {
        switch (order) {
            case PRICE_ASCENDING:
                return Book.byPriceAscending();
            case PRICE_DESCENDING:
                return Book.byPriceDescending();
            case TITLE:
            default:
                return Book.byTitleAscending();
        }
    }

    private int labelFor(@NonNull SortOrder order) {
        switch (order) {
            case PRICE_ASCENDING:
                return R.string.sort_label_price_ascending;
            case PRICE_DESCENDING:
                return R.string.sort_label_price_descending;
            case TITLE:
            default:
                return R.string.sort_label_title;
        }
    }

    /**
     * 创建 50 本书籍数据（均为已出版的现实书籍，价格刻意打乱，便于观察排序效果）。
     */
    private void createBooks() {
        // 计算机
        add("代码整洁之道", "Robert C. Martin", "人民邮电出版社", 59.00, 2010, "计算机",
                "978-7-115-21687-8", "讲述命名、函数、注释、格式与错误处理等编写整洁代码的实践原则。");
        add("重构：改善既有代码的设计", "Martin Fowler", "人民邮电出版社", 99.00, 2019, "计算机",
                "978-7-115-50860-7", "归纳常见的代码坏味道与对应的重构手法，在不改变外部行为的前提下改善内部结构。");
        add("深入理解计算机系统", "Randal E. Bryant", "机械工业出版社", 139.00, 2016, "计算机",
                "978-7-111-54493-7", "从程序员视角讲解程序的机器级表示、存储器层次、链接、异常控制流与并发。");
        add("算法导论", "Thomas H. Cormen", "机械工业出版社", 128.00, 2013, "计算机",
                "978-7-111-40701-0", "系统讲解排序、图算法、动态规划与 NP 完全性等算法设计与分析的核心内容。");
        add("计算机网络：自顶向下方法", "James F. Kurose", "机械工业出版社", 89.00, 2018, "计算机",
                "978-7-111-59971-4", "以应用层为起点自顶向下讲解协议栈，结合实例说明 HTTP、TCP、IP 与网络安全。");
        add("Java 编程思想", "Bruce Eckel", "机械工业出版社", 108.00, 2007, "计算机",
                "978-7-111-21382-6", "从基础语法讲到面向对象、集合、泛型、并发与设计模式，Java 领域公认经典。");
        add("Android 开发艺术探索", "任玉刚", "电子工业出版社", 79.00, 2015, "计算机",
                "978-7-121-26073-1", "围绕 Activity 生命周期、View 绘制、消息机制与线程通信深入剖析 Android 框架。");
        add("数据库系统概念", "Abraham Silberschatz", "机械工业出版社", 118.00, 2012, "计算机",
                "978-7-111-37529-6", "介绍关系模型、SQL、事务、并发控制与恢复机制，兼顾数据库设计与分布式管理。");
        add("设计模式：可复用面向对象软件的基础", "Erich Gamma", "机械工业出版社", 69.00, 2019, "计算机",
                "978-7-111-61179-9", "GoF 四人组归纳的 23 种经典设计模式，是面向对象设计的共同语言。");
        add("人月神话", "Frederick P. Brooks", "清华大学出版社", 49.00, 2015, "计算机",
                "978-7-302-41135-1", "以大型软件项目经验讨论进度估算、团队组织与沟通成本，软件工程必读。");

        // 文学
        add("活着", "余华", "作家出版社", 45.00, 2012, "文学",
                "978-7-5063-6543-1", "通过福贵一生的遭遇，写尽普通人在时代变迁中的坚韧与温情。");
        add("平凡的世界", "路遥", "北京十月文艺出版社", 108.00, 2017, "文学",
                "978-7-5302-1714-3", "以孙少安、孙少平兄弟为主线，展现改革年代陕北城乡青年的奋斗与情感。");
        add("百年孤独", "加西亚·马尔克斯", "南海出版公司", 55.00, 2017, "文学",
                "978-7-5442-8621-0", "魔幻现实主义代表作，讲述布恩迪亚家族七代人的孤独与宿命。");
        add("围城", "钱钟书", "人民文学出版社", 39.00, 1991, "文学",
                "978-7-02-002472-5", "以方鸿渐的经历讽刺知识分子的处境，语言机锋迭出。");
        add("三体", "刘慈欣", "重庆出版社", 23.00, 2008, "文学",
                "978-7-5366-9293-0", "地球文明与三体文明接触的开端，中国科幻里程碑之作。");
        add("白鹿原", "陈忠实", "人民文学出版社", 42.00, 2012, "文学",
                "978-7-02-009127-7", "以白、鹿两家三代人的恩怨，书写渭河平原半个世纪的变迁。");
        add("追风筝的人", "卡勒德·胡赛尼", "上海人民出版社", 36.00, 2006, "文学",
                "978-7-208-06164-4", "关于友谊、背叛与救赎的故事，背景横跨阿富汗与美国。");
        add("小王子", "圣埃克苏佩里", "人民文学出版社", 22.00, 2003, "文学",
                "978-7-02-004143-2", "以童话笔法讲述飞行员与小王子相遇的故事，写尽纯真与责任。");

        // 历史
        add("万历十五年", "黄仁宇", "中华书局", 32.00, 2007, "历史",
                "978-7-101-05252-3", "从万历十五年切入，剖析明代官僚体系的运行与困局。");
        add("中国历代政治得失", "钱穆", "九州出版社", 26.00, 2012, "历史",
                "978-7-5108-1503-1", "以汉、唐、宋、明、清五代为纲，梳理中国传统政治制度的演变。");
        add("人类简史", "尤瓦尔·赫拉利", "中信出版社", 68.00, 2014, "历史",
                "978-7-5086-4735-7", "从认知革命、农业革命到科学革命，重述智人的全球史。");
        add("全球通史", "斯塔夫里阿诺斯", "北京大学出版社", 128.00, 2006, "历史",
                "978-7-301-10948-9", "以全球视角整合各文明的发展脉络，通史类经典教材。");
        add("明朝那些事儿", "当年明月", "浙江人民出版社", 358.00, 2011, "历史",
                "978-7-213-04599-5", "以通俗笔法讲述明朝三百年历史，人物鲜活、脉络清晰。");
        add("史记", "司马迁", "中华书局", 168.00, 2014, "历史",
                "978-7-101-10097-6", "中国第一部纪传体通史，开创本纪、世家、列传的史书体例。");

        // 科普
        add("时间简史", "史蒂芬·霍金", "湖南科学技术出版社", 45.00, 2010, "科普",
                "978-7-5357-3230-9", "面向大众讲解宇宙起源、黑洞与时间本质的科普经典。");
        add("万物简史", "比尔·布莱森", "接力出版社", 58.00, 2005, "科普",
                "978-7-80679-811-3", "以幽默笔调讲述科学发现的历程与科学家的故事。");
        add("自私的基因", "理查德·道金斯", "中信出版社", 69.00, 2012, "科普",
                "978-7-5086-3638-2", "从基因视角解释利他行为与进化，并提出了模因（meme）这一概念。");
        add("上帝掷骰子吗", "曹天元", "北京联合出版公司", 59.80, 2013, "科普",
                "978-7-5502-1436-1", "梳理量子力学百年争论，兼顾物理思想与人物故事。");
        add("寂静的春天", "蕾切尔·卡逊", "上海译文出版社", 39.00, 2011, "科普",
                "978-7-5327-5335-7", "揭示农药滥用对生态的破坏，现代环境保护运动的起点。");
        add("数学之美", "吴军", "人民邮电出版社", 49.00, 2014, "科普",
                "978-7-115-35967-7", "用通信、搜索与自然语言处理实例说明数学模型的简洁之美。");

        // 经济
        add("经济学原理", "格里高利·曼昆", "北京大学出版社", 88.00, 2015, "经济",
                "978-7-301-25962-7", "以十大原理为主线讲解微观与宏观经济学的基本框架。");
        add("国富论", "亚当·斯密", "商务印书馆", 78.00, 2015, "经济",
                "978-7-100-11059-9", "论述分工、市场与看不见的手，是现代经济学的奠基之作。");
        add("穷查理宝典", "彼得·考夫曼", "中信出版社", 98.00, 2016, "经济",
                "978-7-5086-5941-3", "汇集查理·芒格的演讲与思维模型，讲多元思维与长期主义。");
        add("卓有成效的管理者", "彼得·德鲁克", "机械工业出版社", 49.00, 2009, "经济",
                "978-7-111-28000-0", "从时间管理、用人之长与有效决策谈知识工作者的自我管理。");
        add("创新者的窘境", "克莱顿·克里斯坦森", "中信出版社", 58.00, 2014, "经济",
                "978-7-5086-4379-0", "解释优秀企业为何在颠覆性技术面前失败，创新管理经典。");
        add("从0到1", "彼得·蒂尔", "中信出版社", 45.00, 2015, "经济",
                "978-7-5086-4861-4", "讨论创业如何创造垄断性价值，强调从无到有的创新。");

        // 心理
        add("思考，快与慢", "丹尼尔·卡尼曼", "中信出版社", 69.00, 2012, "心理",
                "978-7-5086-3350-1", "提出系统 1 与系统 2 的双系统模型，揭示判断中的偏差。");
        add("乌合之众", "古斯塔夫·勒庞", "中央编译出版社", 25.00, 2005, "心理",
                "978-7-80109-741-9", "分析群体心理的特征与感染机制，社会心理学早期经典。");
        add("影响力", "罗伯特·西奥迪尼", "北京联合出版公司", 68.00, 2016, "心理",
                "978-7-5502-8130-0", "总结互惠、承诺、社会认同等六大说服原理。");
        add("被讨厌的勇气", "岸见一郎", "机械工业出版社", 39.80, 2015, "心理",
                "978-7-111-49541-6", "以对话体介绍阿德勒心理学，讨论课题分离与共同体感觉。");
        add("亲密关系", "罗兰·米勒", "人民邮电出版社", 128.00, 2015, "心理",
                "978-7-115-38345-1", "系统梳理吸引、依恋、沟通与冲突等亲密关系研究成果。");

        // 哲学
        add("论语", "孔子及其弟子", "中华书局", 45.00, 2006, "哲学",
                "978-7-101-05420-6", "儒家核心经典，记录孔子及其弟子的言行。");
        add("沉思录", "马可·奥勒留", "中央编译出版社", 26.00, 2008, "哲学",
                "978-7-80211-578-5", "罗马皇帝的自省笔记，斯多亚学派的实践伦理。");
        add("苏菲的世界", "乔斯坦·贾德", "作家出版社", 38.00, 2007, "哲学",
                "978-7-5063-3947-3", "以小说形式串起西方哲学史，入门读物的经典选择。");
        add("理想国", "柏拉图", "商务印书馆", 56.00, 2002, "哲学",
                "978-7-100-01766-8", "以对话讨论正义、城邦与教育，西方政治哲学的源头。");

        // 艺术
        add("艺术的故事", "贡布里希", "广西美术出版社", 280.00, 2015, "艺术",
                "978-7-5494-1456-0", "从洞穴壁画讲到现代艺术，公认最通俗的艺术史入门。");
        add("西方美术史", "房龙", "江苏人民出版社", 68.00, 2016, "艺术",
                "978-7-214-19035-6", "以讲故事的方式梳理西方绘画、雕塑与建筑的演变。");
        add("美的历程", "李泽厚", "生活·读书·新知三联书店", 59.00, 2009, "艺术",
                "978-7-108-03170-2", "从远古图腾到明清文艺，勾勒中国审美意识的流变。");

        // 童书
        add("安徒生童话", "安徒生", "译林出版社", 35.00, 2010, "童书",
                "978-7-5447-1148-8", "收录《海的女儿》《丑小鸭》等经典童话。");
        add("夏洛的网", "E.B.怀特", "上海译文出版社", 26.00, 2014, "童书",
                "978-7-5327-6606-9", "小猪威尔伯与蜘蛛夏洛的友谊，关于承诺与告别的童话。");
    }

    private void add(String title, String author, String publisher, double price,
                     int year, String category, String isbn, String summary) {
        allBooks.add(new Book(title, author, publisher, price, year, category, isbn, summary));
    }

    /**
     * 适配 Android 15 及以上的强制边到边显示，避免内容被状态栏、导航栏遮挡。
     */
    private void applySystemBarInsets(@NonNull View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
