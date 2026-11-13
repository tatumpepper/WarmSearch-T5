package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.Suser;
import priv.ljh.mapper.SuserMapper;

@Service
public class SuserService extends ServiceImpl<SuserMapper, Suser> {

    public Suser findByUsername(String username) {
        return this.getOne(new QueryWrapper<Suser>().eq("username", username));
    }
}
