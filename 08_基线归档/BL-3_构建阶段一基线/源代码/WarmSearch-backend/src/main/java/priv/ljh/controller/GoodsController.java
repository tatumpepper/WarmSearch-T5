package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.entity.Goods;
import priv.ljh.security.RequireLogin;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.GoodsService;
import priv.ljh.service.ValuablesService;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "招领物品（拾到物品寻失主）")
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private ValuablesService valuablesService;

    @ApiOperation("分页查询招领物品（公开）")
    @GetMapping
    public Result<PageResult<Goods>> list(PageQuery pq,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Long gid,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) Integer isValuable,
                                          @RequestParam(required = false) Long publisherId) {
        return Result.success(goodsService.page(pq, keyword, gid, status, isValuable, publisherId));
    }

    @ApiOperation("物品详情，含详情图（公开）")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(goodsService.getDetail(id));
    }

    @ApiOperation("发布招领物品（学生）；勾选贵重将自动进入保卫处流程")
    @RequireRole("STUDENT")
    @PostMapping
    public Result<Void> add(@RequestBody Goods goods) {
        Long currentId = UserContext.getCurrentId();
        goods.setPublisherId(currentId);
        if (goods.getStatus() == null) {
            goods.setStatus("WAITING");
        }
        goods.setViewCount(0);
        goodsService.save(goods);

        if (goods.getIsValuable() != null && goods.getIsValuable() == 1) {
            valuablesService.createFromGoods(goods, "MANUAL", currentId);
        }
        return Result.success();
    }

    @ApiOperation("修改招领物品")
    @RequireLogin
    @PutMapping
    public Result<Void> update(@RequestBody Goods goods) {
        goodsService.updateById(goods);
        return Result.success();
    }

    @ApiOperation("删除招领物品（管理员/保卫处）")
    @RequireRole({"ADMIN", "SECURITY"})
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        goodsService.removeById(id);
        return Result.success();
    }

    @ApiOperation("管理员/保卫处将物品标记为贵重，触发保卫处流程")
    @RequireRole({"ADMIN", "SECURITY"})
    @PostMapping("/{id}/valuable")
    public Result<Void> markValuable(@PathVariable Long id) {
        Goods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error("物品不存在");
        }
        goods.setIsValuable(1);
        goodsService.updateById(goods);
        valuablesService.createFromGoods(goods, "MANUAL", UserContext.getCurrentId());
        return Result.success();
    }
}
