package com.college.service.edu.controller;


import com.college.commonutils.result.R;
import org.springframework.web.bind.annotation.*;

//模拟登录微服务的类 并没有实现具体的登录功能


@RestController
@RequestMapping("/user")
public class MockLoginController {

    //登录功能
    @PostMapping("/login")
    public R login(){
        return R.ok().data("token","admin");
    }

    //获取用户信息功能 根据前端所需要的字段进行数据传递name、roles、avatar
    @GetMapping("info")
    public R info(){
        return R.ok()
                .data("name","admin")
                .data("roles","[admin]")
                .data("avatar","https://cn.bing.com/images/search?view=detailV2&ccid=mH9YLFEL&id=9745B515FA47E07E4E90B3E8F5624312D6F6400B&thid=OIP.mH9YLFEL5YdVxJM82mjVJQAAAA&mediaurl=https%3a%2f%2fts1.cn.mm.bing.net%2fth%2fid%2fR-C.987f582c510be58755c4933cda68d525%3frik%3dC0D21hJDYvXosw%26riu%3dhttp%253a%252f%252fimg.pconline.com.cn%252fimages%252fupload%252fupc%252ftx%252fwallpaper%252f1305%252f16%252fc4%252f20990657_1368686545122.jpg%26ehk%3dnetN2qzcCVS4ALUQfDOwxAwFcy41oxC%252b0xTFvOYy5ds%253d%26risl%3d%26pid%3dImgRaw%26r%3d0&exph=296&expw=474&q=%e5%9b%be%e7%89%87&simid=608054360452247428&FORM=IRPRST&ck=86E38B9B0828A43E63001C103517C04A&selectedIndex=1");
    }

    //登出功能
    @PostMapping("/logout")
    public R logout(){
        return R.ok();
    }
}
