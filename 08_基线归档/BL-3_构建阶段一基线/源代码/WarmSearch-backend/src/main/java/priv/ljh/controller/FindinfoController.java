package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import priv.ljh.common.PageQuery;
import priv.ljh.common.PageResult;
import priv.ljh.common.Result;
import priv.ljh.entity.Findinfo;
import priv.ljh.security.RequireRole;
import priv.ljh.security.UserContext;
import priv.ljh.service.FindinfoService;

import javax.annotation.Resource;

@Api(tags = "寻物启事（丢失物品寻物）")
@RestController
@RequestMapping("/findinfo")
public class FindinfoController {

    @Resource
    private FindinfoService findinfoService;

    @ApiOperation("分页查询寻物启事（公开）")
    @GetMapping
    public Result<PageResult<Findinfo>> list(PageQuery pq,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) Long publisherId) {
        return Result.success(findinfoService.page(pq, keyword, status, publisherId));
    }

    @ApiOperation("寻物启事详情")
    @GetMapping("/{id}")
    public Result<Findinfo> detail(@PathVariable Long id) {
        return Result.success(findinfoService.getById(id));
    }

    @ApiOperation("发布寻物启事（学生）")
    @RequireRole("STUDENT")
    @PostMapping
    public Result<Void> add(@RequestBody Findinfo findinfo) {
        findinfo.setPublisherId(UserContext.getCurrentId());
        if (findinfo.getFlStatus() == null) {
            findinfo.setFlStatus("OPEN");
        }
        findinfoService.save(findinfo);
        return Result.success();
    }

    @ApiOperation("修改寻物启事")
    @priv.ljh.security.RequireLogin
    @PutMapping
    public Result<Void> update(@RequestBody Findinfo findinfo) {
        findinfoService.updateById(findinfo);
        return Result.success();
    }

    @ApiOperation("删除寻物启事（管理员）")
    @RequireRole("ADMIN")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        findinfoService.removeById(id);
        return Result.success();
    }
}
