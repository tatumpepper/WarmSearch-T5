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
import priv.ljh.constant.CreditConstant;
import priv.ljh.dto.ClaimApplyDTO;
import priv.ljh.entity.*;
import priv.ljh.mapper.ClaimApplicationMapper;
import priv.ljh.mapper.GoodsMapper;
import priv.ljh.security.LoginUser;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 信用认领模块 —— 认领申请服务
 */
@Service
public class ClaimService extends ServiceImpl<ClaimApplicationMapper, ClaimApplication> {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CreditService creditService;
    @Resource
    private AttestationService attestationService;
    @Resource
    private SuserService suserService;

    /**
     * 学生提交认领申请
     */
    @Transactional(rollbackFor = Exception.class)
    public ClaimApplication apply(ClaimApplyDTO dto, LoginUser current) {
        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if ("CLAIMED".equals(goods.getStatus()) || "OFFLINE".equals(goods.getStatus())) {
            throw new BusinessException(ResultCode.GOODS_NOT_WAITING);
        }

        CreditScore credit = creditService.getOrCreate(current.getId());
        if (credit.getScore() < CreditConstant.CLAIM_MIN_SCORE) {
            throw new BusinessException(ResultCode.CREDIT_TOO_LOW);
        }

        Integer exists = this.count(new QueryWrapper<ClaimApplication>()
                .eq("goods_id", dto.getGoodsId())
                .eq("applicant_id", current.getId())
                .in("status", "PENDING", "PASSED"));
        if (exists != null && exists > 0) {
            throw new BusinessException(ResultCode.REPEAT_APPLY);
        }

        ClaimApplication app = new ClaimApplication();
        app.setGoodsId(dto.getGoodsId());
        app.setApplicantId(current.getId());
        app.setApplicantName(current.getName());
        app.setContact(dto.getContact());
        app.setCredentialType(dto.getCredentialType());
        app.setCredentialImg(dto.getCredentialImg());
        app.setDescription(dto.getDescription());
        // 优先级取申请时信用分
        app.setPriority(credit.getScore());
        app.setStatus("PENDING");
        app.setApplyTime(LocalDateTime.now());
        this.save(app);

        if ("WAITING".equals(goods.getStatus())) {
            goods.setStatus("CLAIMING");
            goodsMapper.updateById(goods);
        }
        return app;
    }

    /**
     * 我的认领申请
     */
    public PageResult<ClaimApplication> myPage(PageQuery pq, Long applicantId) {
        Page<ClaimApplication> page = new Page<>(pq.getCurrent(), pq.getSize());
        IPage<ClaimApplication> result = this.page(page, new QueryWrapper<ClaimApplication>()
                .eq("applicant_id", applicantId)
                .orderByDesc("id"));
        return PageResult.of(result);
    }

    /**
     * 后台认领申请列表，默认按优先级（信用分）降序，便于高信用用户优先处理
     */
    public PageResult<ClaimApplication> auditPage(PageQuery pq, String status, Long goodsId) {
        Page<ClaimApplication> page = new Page<>(pq.getCurrent(), pq.getSize());
        QueryWrapper<ClaimApplication> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (goodsId != null) {
            wrapper.eq("goods_id", goodsId);
        }
        wrapper.orderByDesc("priority").orderByAsc("apply_time");
        IPage<ClaimApplication> result = this.page(page, wrapper);
        return PageResult.of(result);
    }

    /**
     * 审核（通过 / 驳回），不代表最终认领
     */
    @Transactional(rollbackFor = Exception.class)
    public ClaimApplication audit(Long id, boolean pass, String remark, LoginUser auditor) {
        ClaimApplication app = this.getById(id);
        if (app == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        app.setStatus(pass ? "PASSED" : "REJECTED");
        app.setAuditTeacherId(auditor.getId());
        app.setAuditRemark(remark);
        app.setAuditTime(LocalDateTime.now());
        this.updateById(app);
        return app;
    }

    /**
     * 最终确认认领：物品置为已认领，其他申请驳回，拾获者加分，记录认领台账
     */
    @Transactional(rollbackFor = Exception.class)
    public ClaimApplication confirm(Long claimId, LoginUser auditor) {
        ClaimApplication app = this.getById(claimId);
        if (app == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if ("CONFIRMED".equals(app.getStatus()) || "FALSE".equals(app.getStatus())) {
            throw new BusinessException("该申请已结束，不可重复确认");
        }

        app.setStatus("CONFIRMED");
        app.setConfirmTime(LocalDateTime.now());
        app.setAuditTeacherId(auditor.getId());
        this.updateById(app);

        Goods goods = goodsMapper.selectById(app.getGoodsId());
        if (goods != null) {
            goods.setStatus("CLAIMED");
            goodsMapper.updateById(goods);

            // 同一物品其他进行中的申请统一驳回
            ClaimApplication reject = new ClaimApplication();
            reject.setStatus("REJECTED");
            this.update(reject, new QueryWrapper<ClaimApplication>()
                    .eq("goods_id", goods.getId())
                    .ne("id", app.getId())
                    .in("status", "PENDING", "PASSED"));

            // 拾获者（发布者）信用加分
            if (goods.getPublisherId() != null) {
                creditService.returnBonus(goods.getPublisherId(), goods.getId(),
                        "成功归还物品：" + goods.getImgName());
            }
        }

        // 普通认领台账
        Attestation attestation = new Attestation();
        attestation.setGoodsId(app.getGoodsId());
        attestation.setGoodsName(goods == null ? null : goods.getImgName());
        attestation.setUserId(app.getApplicantId());
        attestation.setUserName(app.getApplicantName());
        attestation.setContact(app.getContact());
        Suser applicant = suserService.getById(app.getApplicantId());
        attestation.setNumber(applicant == null ? null : applicant.getUsername());
        attestation.setImgUrl(app.getCredentialImg());
        attestation.setClaimTime(LocalDateTime.now());
        attestationService.save(attestation);

        return app;
    }

    /**
     * 判定为虚假认领：申请置为 FALSE，并对申请人扣分
     */
    @Transactional(rollbackFor = Exception.class)
    public ClaimApplication markFalse(Long claimId, LoginUser auditor) {
        ClaimApplication app = this.getById(claimId);
        if (app == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        app.setStatus("FALSE");
        app.setAuditTeacherId(auditor.getId());
        app.setAuditTime(LocalDateTime.now());
        this.updateById(app);

        creditService.falseClaimPenalty(app.getApplicantId(), app.getId());
        return app;
    }
}
