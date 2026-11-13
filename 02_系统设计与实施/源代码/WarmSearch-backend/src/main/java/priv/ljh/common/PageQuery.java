package priv.ljh.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基础参数
 */
@Data
public class PageQuery implements Serializable {

    private Integer page = 1;
    private Integer limit = 10;
    /** 排序：+id 升序，-id 降序 */
    private String sort;

    public long getCurrent() {
        return page == null || page < 1 ? 1 : page;
    }

    public long getSize() {
        return limit == null || limit < 1 ? 10 : Math.min(limit, 100);
    }
}
