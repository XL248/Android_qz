

## 一、题目要求与实现位置

| 题目要求 | 实现位置 |
| --- | --- |
| 项目名包含姓名拼音 | `settings.gradle.kts` → `rootProject.name = "BaiXinYuan_Android_qz"` |
| 书籍类（书名、作者、出版社、价格等） | `model/Book.java`（另含年份、分类、ISBN、简介；`Serializable`） |
| 代码里新建多个书籍对象放入数组 | `MainActivity.createBooks()` 创建 **50 本**现实书籍 |
| 排序 | 默认 `Book.byTitleAscending()`（中文按拼音）；可切 `byPriceAscending()` / `byPriceDescending()` |
| 采用列表显示 | `RecyclerView` + `BookAdapter`（`ListAdapter` + `DiffUtil`）+ `item_book.xml` |
| 单击书籍启动第 2 个 Activity | 点击回调 → `Intent.putExtra(EXTRA_BOOK, book)` → `startActivity` |
| 第 2 个 Activity 显示完整信息 | `DetailActivity` + `activity_detail.xml`（8 个字段） |
| 文字颜色按尾号 | `colors.xml` 全套绿色（`text_primary #1B5E20` 等） |
| 布局合理、无错误警告 | `clean assembleDebug` 成功无告警；单元测试 6 个全过 |

## 二、扩展功能

1. **50 本书**：计算机 10、文学 8、历史 6、科普 6、经济 6、心理 5、哲学 4、艺术 3、童书 2，均为已出版书籍。
2. **滚动浏览**：列表用 `RecyclerView`，支持鼠标滚轮、拖动与触摸滑动，右侧常显滚动条；顶部排序栏与分类栏各自横向可滚动，滚动列表时固定不动。
3. **排序切换**：顶部三个标签——书名首字母（默认）/ 价格从低到高 / 价格从高到低，副标题实时显示当前排序方式。
4. **分类筛选**：第二行标签由代码按数据动态生成（全部 + 9 个分类），与排序叠加生效，切换后自动回到列表顶部。

## 三、目录结构

```
app/src/main/java/cn/edu/sicnu/cs/stu/baixinyuan/android_qz/
├── MainActivity.java          造 50 本书 → 排序 / 分类筛选 → 列表 → 跳转
├── DetailActivity.java        接收 Book 并展示全部字段
├── model/Book.java            实体 + 拼音/价格比较器 + 分类筛选
└── adapter/BookAdapter.java   ListAdapter + DiffUtil + 点击回调

app/src/main/res/
├── layout/activity_main.xml   工具栏 + 副标题 + 排序栏 + 分类栏 + RecyclerView
├── layout/item_book.xml       列表项：书名 / 作者·出版社·分类 / 价格
├── layout/activity_detail.xml 详情卡片 + 返回按钮
├── color/selector_chip_*.xml  标签选中/未选中配色
├── drawable/ic_back.xml       返回箭头
└── values/{colors,strings,themes}.xml

app/src/test/java/.../BookSortTest.java   6 个用例（拼音排序 / 价格升降 / 分类筛选 / 价格格式）
```

## 四、运行

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n cn.edu.sicnu.cs.stu.baixinyuan.android_qz/.MainActivity
```

环境：JDK 17、AGP 9.3.3、compileSdk 37、minSdk 24、targetSdk 37。
