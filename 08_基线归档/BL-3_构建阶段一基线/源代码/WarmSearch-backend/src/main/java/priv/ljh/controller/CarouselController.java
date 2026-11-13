package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.Result;
import priv.ljh.entity.Carousel;
import priv.ljh.security.RequireRole;
import priv.ljh.service.CarouselService;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "轮播图")
@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Resource
    private CarouselService carouselService;

    @ApiOperation("按类型查询轮播图（公开）：MINI小程序 / PC电脑端")
    @GetMapping
    public Result<List<Carousel>> list(@RequestParam(defaultValue = "MINI") String type) {
        return Result.success(carouselService.listByType(type));
    }

    @ApiOperation("新增轮播图（管理员）")
    @RequireRole("ADMIN")
    @PostMapping
    public Result<Void> add(@RequestBody Carousel carousel) {
        carouselService.save(carousel);
        return Result.success();
    }

    @ApiOperation("修改轮播图（管理员）")
    @RequireRole("ADMIN")
    @PutMapping
    public Result<Void> update(@RequestBody Carousel carousel) {
        carouselService.updateById(carousel);
        return Result.success();
    }

    @ApiOperation("删除轮播图（管理员）")
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        carouselService.removeById(id);
        return Result.success();
    }
}
