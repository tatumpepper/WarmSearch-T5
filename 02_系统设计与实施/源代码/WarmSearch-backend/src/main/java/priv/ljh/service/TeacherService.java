package priv.ljh.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.ljh.entity.Teacher;
import priv.ljh.mapper.TeacherMapper;

@Service
public class TeacherService extends ServiceImpl<TeacherMapper, Teacher> {

    public Teacher findByUsername(String username) {
        return this.getOne(new QueryWrapper<Teacher>().eq("username", username));
    }
}
