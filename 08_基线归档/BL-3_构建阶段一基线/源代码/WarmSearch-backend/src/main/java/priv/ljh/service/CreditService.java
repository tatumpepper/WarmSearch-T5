package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.ljh.constant.CreditConstant;
import priv.ljh.entity.CreditLog;
import priv.ljh.entity.CreditScore;
import priv.ljh.mapper.CreditLogMapper;
import priv.ljh.mapper.CreditScoreMapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * 信用认领模块 —— 信用分核心服务
 */
@Service
public class CreditService extends ServiceImpl<CreditScoreMapper, CreditScore> {

    @Resource
    private CreditLogMapper creditLogMapper;

    /**
     * 获取信用档案，不存在则按初始分创建
     */
    public CreditScore getOrCreate(Long userId) {
        CreditScore score = this.getOne(new QueryWrapper<CreditScore>().eq("user_id", userId));
        if (score == null) {
            score = new CreditScore();
            score.setUserId(userId);
            score.setScore(CreditConstant.INIT_SCORE);
            score.setLevel(CreditConstant.calcLevel(CreditConstant.INIT_SCORE));
            score.setReturnCount(0);
            score.setFalseClaimCount(0);
            this.save(score);
        }
        return score;
    }

    /**
     * 信用分变动（加分 / 扣分），同时记录流水
     *
     * @param changeValue 正数加分，负数扣分
     * @param type        RETURN / FALSE_CLAIM / PUNISH / MANUAL
     */
    @Transactional(rollbackFor = Exception.class)
    public CreditScore changeCredit(Long userId, int changeValue, String type,
                                    String reason, Long bizId, Long operatorId) {
        CreditScore score = getOrCreate(userId);
        int newScore = score.getScore() + changeValue;
        if (newScore < 0) {
            newScore = 0;
        }
        score.setScore(newScore);
        score.setLevel(CreditConstant.calcLevel(newScore));
        if (CreditConstant.RETURN_BONUS == changeValue || "RETURN".equals(type)) {
            score.setReturnCount(score.getReturnCount() + 1);
        }
        if ("FALSE_CLAIM".equals(type)) {
            score.setFalseClaimCount(score.getFalseClaimCount() + 1);
        }
        this.updateById(score);

        CreditLog log = new CreditLog();
        log.setUserId(userId);
        log.setChangeValue(changeValue);
        log.setType(type);
        log.setReason(reason);
        log.setBizId(bizId);
        log.setOperatorId(operatorId);
        creditLogMapper.insert(log);

        return score;
    }

    /**
     * 拾获者成功归还物品 —— 加分
     */
    public CreditScore returnBonus(Long userId, Long goodsId, String reason) {
        return changeCredit(userId, CreditConstant.RETURN_BONUS, "RETURN", reason, goodsId, null);
    }

    /**
     * 虚假认领 —— 扣分
     */
    public CreditScore falseClaimPenalty(Long userId, Long claimId) {
        return changeCredit(userId, CreditConstant.FALSE_CLAIM_PENALTY, "FALSE_CLAIM",
                "认领申请被判定为虚假认领", claimId, null);
    }

    /**
     * 信用积分排行榜
     */
    public List<CreditScore> leaderboard(int limit) {
        return this.list(new QueryWrapper<CreditScore>()
                .orderByDesc("score")
                .orderByAsc("id")
                .last("limit " + Math.max(1, Math.min(limit, 100))));
    }
}
