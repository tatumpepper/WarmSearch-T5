package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.Carousel;
import priv.ljh.mapper.CarouselMapper;

import java.util.List;

@Service
public class CarouselService extends ServiceImpl<CarouselMapper, Carousel> {

    public List<Carousel> listByType(String type) {
        return this.list(new QueryWrapper<Carousel>()
                .eq("type", type)
                .eq("status", 1)
                .orderByAsc("sort"));
    }
}
