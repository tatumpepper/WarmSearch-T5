package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.CreditLog;
import priv.ljh.mapper.CreditLogMapper;

import java.util.List;

@Service
public class CreditLogService extends ServiceImpl<CreditLogMapper, CreditLog> {

    public List<CreditLog> listByUserId(Long userId) {
        return this.list(new QueryWrapper<CreditLog>()
                .eq("user_id", userId)
                .orderByDesc("id"));
    }
}
