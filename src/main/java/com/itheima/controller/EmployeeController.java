package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.pojo.Employee;
import com.itheima.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;


    //员工登陆：
    @PostMapping("/login" )
    public R<Employee> login(HttpServletRequest request,  @RequestBody Employee employee){


        //1.将页面提交的密码进行md5加密；
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. 根据页面提交的用户名查询数据库；
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername() );
        Employee emp = employeeService.getOne(wrapper);


        //3.如果没有查询到，返回登陆失败ms；
        if(emp == null){
            return R.error("用户名不存在，登陆失败，请重新输入");
        }



        //4.密码比对，如果不一致，返回失败ms；
        if(! emp.getPassword().equals(password)){
            return R.error("密码输入错误，登陆失败，请重新输入");
        }

        //5.查看员工禁用状态，如果为0，返回禁用ms；
        if(emp.getStatus() == 0){
            return R.error("此员工已被禁用");
        }


        //6.登陆成功，把员工id存入session，返回登陆成功的data。
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }


    //员工退出登陆：
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        // 清理session中这个员工登陆时候保存的员工id信息：
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }




    //员工页面 -> 新增一个员工：
    //把页面上没有，但数据库表里有的字段，自己添上：（status不用写，因为设计数据库的时候已经设置了默认值是1）
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request){

        //统一分发一个初始密码（md5加密）
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //假如没设置-meta handler-公共字段自动填充：
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //想获得当前用户的id , 从session里往出拿
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/


        employeeService.save(employee);
        return R.success("新增员工成功");
    }




    //分页查询员工信息：**返回IPage的实现类, //根据浏览器页面inspector里来决定参数和请求路径
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();

        //过滤条件
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }



        //员工账号的禁用/启用 ——> 本质上是一个更新操作：
        //员工信息编辑-> 也是一个更新操作
        //所以2个调这一个update方法就可以了：
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request){

       /* Long empId = (Long) request.getSession().getAttribute("employee");//取出当前用户是谁

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");

    }


    //编辑/新增员工：
        //根据id来查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee thisEmployee = employeeService.getById(id);
        if (thisEmployee != null){
            return R.success(thisEmployee);
        }
        return R.error("没有查询到此人");
    }















}
