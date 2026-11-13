package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.entity.Goods;
import priv.ljh.mapper.GoodsMapper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 招领物品服务（拾到物品，寻失主）
 */
@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {

    @Resource
    private GoodsDetailService goodsDetailService;

    public PageResult<Goods> page(PageQuery pq, String keyword, Long gid, String status, Integer isValuable, Long publisherId) {
        Page<Goods> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("img_name", keyword).or().like("img_desc", keyword));
        }
        if (gid != null) {
            wrapper.eq("gid", gid);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (isValuable != null) {
            wrapper.eq("is_valuable", isValuable);
        }
        if (publisherId != null) {
            wrapper.eq("publisher_id", publisherId);
        }
        if ("+id".equals(pq.getSort())) {
            wrapper.orderByAsc("id");
        } else {
            wrapper.orderByDesc("id");
        }
        IPage<Goods> result = this.page(page, wrapper);
        return PageResult.of(result);
    }

    /**
     * 物品详情（含详情图），同时累加点击量
     */
    public Map<String, Object> getDetail(Long id) {
        Goods goods = this.getById(id);
        if (goods == null) {
            return null;
        }
        goods.setViewCount(goods.getViewCount() == null ? 1 : goods.getViewCount() + 1);
        this.updateById(goods);

        Map<String, Object> map = new HashMap<>();
        map.put("goods", goods);
        map.put("details", goodsDetailService.listByGoodsId(id));
        return map;
    }
}
