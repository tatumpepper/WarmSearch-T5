package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.GoodsDetail;
import priv.ljh.mapper.GoodsDetailMapper;

import java.util.List;

@Service
public class GoodsDetailService extends ServiceImpl<GoodsDetailMapper, GoodsDetail> {

    public List<GoodsDetail> listByGoodsId(Long goodsId) {
        return this.list(new QueryWrapper<GoodsDetail>()
                .eq("goods_id", goodsId)
                .orderByAsc("sort"));
    }
}
