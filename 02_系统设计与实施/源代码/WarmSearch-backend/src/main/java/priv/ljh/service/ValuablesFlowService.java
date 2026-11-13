package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.ValuablesFlow;
import priv.ljh.mapper.ValuablesFlowMapper;

import java.util.List;

@Service
public class ValuablesFlowService extends ServiceImpl<ValuablesFlowMapper, ValuablesFlow> {

    public List<ValuablesFlow> listByValuablesId(Long valuablesId) {
        return this.list(new QueryWrapper<ValuablesFlow>()
                .eq("valuables_id", valuablesId)
                .orderByAsc("id"));
    }
}
