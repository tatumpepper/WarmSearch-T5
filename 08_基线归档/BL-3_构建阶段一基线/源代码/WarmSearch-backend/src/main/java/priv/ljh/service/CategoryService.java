package priv.ljh.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.Category;
import priv.ljh.mapper.CategoryMapper;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {
}
