package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.Result;
import priv.ljh.entity.Category;
import priv.ljh.security.RequireRole;
import priv.ljh.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "物品分类")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @ApiOperation("分类列表（公开）")
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list(null));
    }

    @ApiOperation("分类详情")
    @GetMapping("/{id}")
    public Result<Category> get(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @ApiOperation("新增分类（管理员）")
    @RequireRole("ADMIN")
    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    @ApiOperation("修改分类（管理员）")
    @RequireRole("ADMIN")
    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    @ApiOperation("删除分类（管理员）")
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
