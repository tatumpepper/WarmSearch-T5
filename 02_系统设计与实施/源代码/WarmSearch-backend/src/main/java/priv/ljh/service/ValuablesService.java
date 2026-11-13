package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.ljh.common.BusinessException;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.ResultCode;
import priv.ljh.dto.ValuablesClaimDTO;
import priv.ljh.entity.Goods;
import priv.ljh.entity.ValuablesFlow;
import priv.ljh.entity.ValuablesItem;
import priv.ljh.mapper.GoodsMapper;
import priv.ljh.mapper.ValuablesFlowMapper;
import priv.ljh.mapper.ValuablesItemMapper;
import priv.ljh.security.LoginUser;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 保卫处贵重物品处理模块 —— 贵重物品服务
 */
@Service
public class ValuablesService extends ServiceImpl<ValuablesItemMapper, ValuablesItem> {

    @Resource
    private ValuablesFlowMapper flowMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CreditService creditService;

    /**
     * 由招领物品生成贵重物品记录（拾获者标记或管理员标记时调用）
     */
    @Transactional(rollbackFor = Exception.class)
    public ValuablesItem createFromGoods(Goods goods, String source, Long operatorId) {
        ValuablesItem exists = this.getOne(new QueryWrapper<ValuablesItem>().eq("goods_id", goods.getId()));
        if (exists != null) {
            return exists;
        }
        ValuablesItem item = new ValuablesItem();
        item.setGoodsId(goods.getId());
        item.setItemName(goods.getImgName());
        item.setImgUrl(goods.getImgUrl());
        item.setDescription(goods.getImgDesc());
        item.setReportUserId(operatorId != null ? operatorId : goods.getPublisherId());
        item.setReportSource(source);
        item.setStatus("PENDING_AUDIT");
        item.setReportTime(LocalDateTime.now());
        this.save(item);

        recordFlow(item, "REPORT", null, "PENDING_AUDIT", null, null, operatorId, null);
        return item;
    }

    public PageResult<ValuablesItem> page(PageQuery pq, String status) {
        Page<ValuablesItem> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<ValuablesItem> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("id");
        IPage<ValuablesItem> result = this.page(page, wrapper);
        return PageResult.of(result);
    }

    /**
     * 保卫处审核：通过 → 入库保管；驳回 → 审核驳回
     */
    @Transactional(rollbackFor = Exception.class)
    public ValuablesItem audit(Long id, boolean pass, String remark, LoginUser security) {
        ValuablesItem item = mustGet(id);
        if (pass) {
            item.setStatus("IN_CUSTODY");
            item.setSecurityId(security.getId());
            this.updateById(item);
            recordFlow(item, "AUDIT_PASS", "PENDING_AUDIT", "IN_CUSTODY", null, remark, security.getId(), security.getName());
        } else {
            item.setStatus("REJECTED");
            this.updateById(item);
            recordFlow(item, "REJECT", "PENDING_AUDIT", "REJECTED", null, remark, security.getId(), security.getName());
        }
        return item;
    }

    /**
     * 入库保管登记（保管位置）
     */
    @Transactional(rollbackFor = Exception.class)
    public ValuablesItem custody(Long id, String location, LoginUser security) {
        ValuablesItem item = mustGet(id);
        String from = item.getStatus();
        item.setStatus("IN_CUSTODY");
        item.setCustodyLocation(location);
        item.setSecurityId(security.getId());
        this.updateById(item);
        recordFlow(item, "CUSTODY", from, "IN_CUSTODY", null, "保管位置：" + location, security.getId(), security.getName());
        return item;
    }

    /**
     * 认领人提交认领（购买凭证 / 身份证明），物品进入核验中
     */
    @Transactional(rollbackFor = Exception.class)
    public ValuablesItem submitClaim(ValuablesClaimDTO dto, LoginUser current) {
        ValuablesItem item = mustGet(dto.getValuablesId());
        if (!"IN_CUSTODY".equals(item.getStatus())) {
            throw new BusinessException("物品尚未入库保管，暂不能提交认领");
        }
        item.setStatus("IN_VERIFICATION");
        this.updateById(item);
        recordFlow(item, "CLAIM_APPLY", "IN_CUSTODY", "IN_VERIFICATION", dto, dto.getRemark(), current.getId(), current.getName());
        return item;
    }

    /**
     * 保卫处线下核验：通过 → 认领完成；不通过 → 退回保管
     */
    @Transactional(rollbackFor = Exception.class)
    public ValuablesItem verify(Long id, boolean pass, String remark, LoginUser security) {
        ValuablesItem item = mustGet(id);
        if (pass) {
            item.setStatus("CLAIMED");
            item.setSecurityId(security.getId());
            this.updateById(item);
            recordFlow(item, "CLAIM_COMPLETE", "IN_VERIFICATION", "CLAIMED", null, remark, security.getId(), security.getName());

            Goods goods = goodsMapper.selectById(item.getGoodsId());
            if (goods != null) {
                goods.setStatus("CLAIMED");
                goodsMapper.updateById(goods);
                if (goods.getPublisherId() != null) {
                    creditService.returnBonus(goods.getPublisherId(), goods.getId(),
                            "保卫处核验通过，成功归还贵重物品：" + item.getItemName());
                }
            }
        } else {
            item.setStatus("IN_CUSTODY");
            item.setSecurityId(security.getId());
            this.updateById(item);
            recordFlow(item, "REJECT", "IN_VERIFICATION", "IN_CUSTODY", null, "核验未通过：" + remark, security.getId(), security.getName());
        }
        return item;
    }

    private ValuablesItem mustGet(Long id) {
        ValuablesItem item = this.getById(id);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return item;
    }

    private void recordFlow(ValuablesItem item, String action, String from, String to,
                            ValuablesClaimDTO claim, String remark, Long operatorId, String operatorName) {
        ValuablesFlow flow = new ValuablesFlow();
        flow.setValuablesId(item.getId());
        flow.setGoodsId(item.getGoodsId());
        flow.setAction(action);
        flow.setFromStatus(from);
        flow.setToStatus(to);
        if (claim != null) {
            flow.setClaimerName(claim.getClaimerName());
            flow.setClaimerContact(claim.getClaimerContact());
            flow.setClaimerNumber(claim.getClaimerNumber());
            flow.setPurchaseProofImg(claim.getPurchaseProofImg());
            flow.setIdProofImg(claim.getIdProofImg());
        }
        flow.setVerifyRemark(remark);
        flow.setOperatorId(operatorId);
        flow.setOperatorName(operatorName);
        flow.setOperateTime(LocalDateTime.now());
        flowMapper.insert(flow);
    }
}
