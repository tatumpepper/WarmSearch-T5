package priv.ljh.constant;

/**
 * 信用认领相关常量与规则
 */
public class CreditConstant {

    /** 初始信用分 */
    public static final int INIT_SCORE = 100;

    /** 拾获者成功归还物品 加分 */
    public static final int RETURN_BONUS = 10;

    /** 虚假认领 扣分（负数） */
    public static final int FALSE_CLAIM_PENALTY = -20;

    /** 恶意行为 扣分（负数） */
    public static final int MALICIOUS_PENALTY = -30;

    /** 信用分低于该值，禁止提交认领申请 */
    public static final int CLAIM_MIN_SCORE = 60;

    /** 信用分达到该值，认领同一物品时享有优先权 */
    public static final int PRIORITY_THRESHOLD = 100;

    /** 等级：优秀 */
    public static final String LEVEL_EXCELLENT = "EXCELLENT";
    /** 等级：良好 */
    public static final String LEVEL_GOOD = "GOOD";
    /** 等级：一般 */
    public static final String LEVEL_NORMAL = "NORMAL";
    /** 等级：较差 */
    public static final String LEVEL_POOR = "POOR";

    /**
     * 根据信用分计算等级
     */
    public static String calcLevel(int score) {
        if (score >= 150) {
            return LEVEL_EXCELLENT;
        }
        if (score >= 100) {
            return LEVEL_GOOD;
        }
        if (score >= 60) {
            return LEVEL_NORMAL;
        }
        return LEVEL_POOR;
    }
}
