
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>页脚</title>
</head>
<body>
    <div class="footer">
        <div>
            <p>鹰眼电影网上购票系统</p>
            <p>
                友情链接：
                <a href="https://maoyan.com/" target="_blank"
                   rel="noopener noreferrer"
                   style="margin-right:30px;">
                    猫眼电影
                </a>
                <a href="https://www.themoviedb.org/" target="_blank"
                   rel="noopener noreferrer"
                   style="margin-right:30px;">
                    TMDB 电影数据库
                </a>
                <span onclick="footerBtn()"
                      style="height:20px; cursor:pointer;">
                    客服：
                    <img src="../static/images/客服.jpg"
                         alt="客服"
                         style="width:20px;">
                </span>
            </p>
            <p>
                本项目部分电影信息及图片来源于
                <a href="https://www.themoviedb.org/"
                   target="_blank"
                   rel="noopener noreferrer">
                    The Movie Database (TMDB)
                </a>。
                <br>
                This product uses the TMDB API but is not endorsed
                or certified by TMDB.
            </p>
            <p>
                            组长：翁修杰
                            小组成员：梁颂伟、魏泽伟、黄泽伟、李鹏飞
                            <br>
                            后台：翁修杰
                            前端：梁颂伟
                            前端协助：魏泽伟、黄泽伟、李鹏飞
                        </p>
                        <p>©2019 版权归小组成员所有</p>

            <p>
                本项目在原项目基础上进行二次开发。
            </p>
        </div>
    </div>

    <script>
        function footerBtn() {
            layui.use(['layer'], function () {
                var layer = layui.layer;

                layer.open({
                    type: 1,
                    title: "客服",
                    closeBtn: 1,
                    area: ['400px', 'auto'],
                    shade: 0.8,
                    offset: 'auto',
                    btn: ['确认', '取消'],
                    btnAlign: 'c',
                    content:
                        '<div style="text-align:center; margin:20px 0 10px;">' +
                        '<img src="../static/images/美女客服.jpg" ' +
                        'alt="客服图片" style="width:320px; max-width:100%;">' +
                        '</div>'
                });
            });
        }
    </script>
</body>
</html>
