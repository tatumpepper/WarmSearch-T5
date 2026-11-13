package priv.ljh.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果，字段 items / total 与 WarmSearch-Web 前端约定保持一致
 */
@Data
public class PageResult<T> implements Serializable {

    private List<T> items;
    private long total;

    public PageResult() {
    }

    public PageResult(List<T> items, long total) {
        this.items = items;
        this.total = total;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    public static <T> PageResult<T> of(List<T> items, long total) {
        return new PageResult<>(items, total);
    }
}
