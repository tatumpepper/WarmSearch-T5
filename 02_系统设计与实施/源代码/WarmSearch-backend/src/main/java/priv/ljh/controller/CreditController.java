package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.Result;
import priv.ljh.entity.CreditScore;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.CreditLogService;
import priv.ljh.service.CreditService;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "信用认领-信用分")
@RestController
@RequestMapping("/credit")
public class CreditController {

    @Resource
    private CreditService creditService;
    @Resource
    private CreditLogService creditLogService;

    @ApiOperation("查看我的信用分（学生）")
    @RequireRole("STUDENT")
    @GetMapping("/my")
    public Result<CreditScore> my() {
        return Result.success(creditService.getOrCreate(UserContext.getCurrentId()));
    }

    @ApiOperation("查看我的信用变动流水（学生）")
    @RequireRole("STUDENT")
    @GetMapping("/my/logs")
    public Result<List<priv.ljh.entity.CreditLog>> myLogs() {
        return Result.success(creditLogService.listByUserId(UserContext.getCurrentId()));
    }

    @ApiOperation("信用积分排行榜（公开）")
    @GetMapping("/leaderboard")
    public Result<List<CreditScore>> leaderboard(@RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(creditService.leaderboard(limit));
    }

    @ApiOperation("查看指定用户信用分")
    @GetMapping("/{userId}")
    public Result<CreditScore> get(@PathVariable Long userId) {
        return Result.success(creditService.getOrCreate(userId));
    }

    @ApiOperation("管理员人工调整信用分")
    @RequireRole("ADMIN")
    @PostMapping("/adjust")
    public Result<Void> adjust(@RequestParam Long userId,
                               @RequestParam Integer change,
                               @RequestParam(required = false) String reason) {
        creditService.changeCredit(userId, change, "MANUAL", reason, null, UserContext.getCurrentId());
        return Result.success();
    }
}
