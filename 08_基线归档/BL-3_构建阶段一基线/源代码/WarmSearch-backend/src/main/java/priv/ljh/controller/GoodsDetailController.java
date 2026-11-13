package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.Result;
import priv.ljh.entity.GoodsDetail;
import priv.ljh.security.RequireLogin;
import priv.ljh.service.GoodsDetailService;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "物品详情图")
@RestController
@RequestMapping("/goodsdetail")
public class GoodsDetailController {

    @Resource
    private GoodsDetailService goodsDetailService;

    @ApiOperation("按物品查询详情图（公开）")
    @GetMapping
    public Result<List<GoodsDetail>> list(@RequestParam Long goodsId) {
        return Result.success(goodsDetailService.listByGoodsId(goodsId));
    }

    @ApiOperation("新增详情图")
    @RequireLogin
    @PostMapping
    public Result<Void> add(@RequestBody GoodsDetail detail) {
        goodsDetailService.save(detail);
        return Result.success();
    }

    @ApiOperation("删除详情图")
    @RequireLogin
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        goodsDetailService.removeById(id);
        return Result.success();
    }
}
