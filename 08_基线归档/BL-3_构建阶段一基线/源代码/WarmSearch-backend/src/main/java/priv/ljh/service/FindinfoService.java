package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.entity.Findinfo;
import priv.ljh.mapper.FindinfoMapper;

/**
 * 寻物启事服务（丢失物品，寻物）
 */
@Service
public class FindinfoService extends ServiceImpl<FindinfoMapper, Findinfo> {

    public PageResult<Findinfo> page(PageQuery pq, String keyword, String status, Long publisherId) {
        Page<Findinfo> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<Findinfo> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("fl_img_desc", keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq("fl_status", status);
        }
        if (publisherId != null) {
            wrapper.eq("publisher_id", publisherId);
        }
        if ("+id".equals(pq.getSort())) {
            wrapper.orderByAsc("id");
        } else {
            wrapper.orderByDesc("id");
        }
        IPage<Findinfo> result = this.page(page, wrapper);
        return PageResult.of(result);
    }
}
