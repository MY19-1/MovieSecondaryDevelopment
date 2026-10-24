<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">

    <link href="../static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="../static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <script src="../static/bootstrap/js/jquery-3.3.1.min.js"></script>
    <script src="../static/bootstrap/js/bootstrap.min.js"></script>
    <script src="../static/js/Api.js"></script>
    <link rel="icon" type="image/x-icon" href="../static/images/logo.ico"/>
    <link href="../static/css/login.css" rel="stylesheet">

    <script></script>
    <title>鹰眼电影-登录注册</title>
</head>
<body>

    <!-- ------------------------------------------------------------------- -->
    <div class="screen">
        <img class="big_logo"><br/>
        <label class="title">鹰 眼 电 影</label><br/>
        <label class="ineer_one">回首向来风雨茶香里</label><br/>
        <label class="ineer_two">眸中一点可是思念谁</label>
    </div>
    <!-- 登录页 -->
    <div class="content" style="float: left;">
        <ul class="tab">
            <li class="login">登录</li>
            <li class="register">注册</li>
        </ul>
        <div class="page">
            <!-- 登录界面 -->
            <div class="childpage" style="display: block">
                <div>
                    <label class="login_title">用户登录</label><br/>
                </div>
                <div class="login_page">
                    <div>
                        <label>帐号</label><br/>
                        <input id="UserName" type="text"/>
                    </div>
                    <div>
                        <label>密码</label><br/>
                        <input id="PassWord" type="password"/>
                    </div>
                </div>
                <div class="lo_error">
                    <label class="login_error"></label>
                </div>
                <div>
                    <input type="button" value="登录" class="btn btn-success login_btn" onclick="loginbtn()"/>
                </div>
                <div>
                    <label class="login_version">@版权所有</label>
                </div>
            </div>
            <!-- 注册界面 -->
            <!-- 注册界面 -->
            <div class="childpage" style="display: none">

                <div class="register_page">

                    <div>
                        <label>用户名</label><br/>
                        <input id="UserName"
                               type="text"
                               placeholder="请输入用户名"/>
                    </div>

                    <div>
                        <label>密码</label><br/>
                        <input id="PassWord"
                               type="password"
                               placeholder="请输入密码"/>
                    </div>

                    <div>
                        <label>邮箱</label><br/>
                        <input id="Email"
                               type="email"
                               placeholder="请输入邮箱"/>
                    </div>

                    <div>
                        <label>邮箱验证码</label><br/>
                        <input id="EmailCode"
                               type="text"
                               placeholder="请输入邮箱验证码"
                               maxlength="6"/>

                        <button type="button"
                                id="SendEmailCode"
                                class="btn btn-info">
                            获取邮箱验证码
                        </button>
                    </div>

                </div>

                <div class="re_error">
                    <label class="register_error"></label>
                </div>

                <div>
                    <input type="button"
                           value="注册"
                           class="btn btn-warning register_btn"
                           onclick="registerbtn()"/>
                </div>

                <div>
                    <label class="register_version">@版权所有</label>
                </div>

            </div>
        
    </div>

    <!-- ------------------------------------------------------------------- -->
    <script>
        //初始化
        window.onload = function(){ 
            initWindow(); //初始化登录框位置
            initLogin(); //初始化登录界面
    　　}; 

        //初始化登录框位置（垂直居中、水平4/7）
        function initWindow(){
            var middle = 3;
            var colorNum = 200;
            var clientHeight = document.documentElement.clientHeight;
            var clientWidth = document.documentElement.clientWidth;
            var content = document.getElementsByClassName('content')[0];
            var screen = document.getElementsByClassName('screen')[0];
            var title = document.getElementsByClassName('title')[0];
            var bodys = document.getElementsByTagName('body')[0];
            bodys.style.cssText = "background-size: " + clientWidth + "px auto;";
            content.style.cssText = "margin:" + (clientHeight - content.clientHeight)/2 +"px " + 
            clientWidth*4/7 + "px " +
            (clientHeight - content.clientHeight)/2 + "px;";
            screen.style.cssText = "margin:" + (content.clientHeight - screen.clientHeight)/2 +"px " + 
            (clientWidth*4/7 - screen.clientWidth)/2 + "px " +
            (content.clientHeight - screen.clientHeight)/2 + "px;";
            setInterval(function(){
                colorNum += middle;
                if(colorNum>185){
                    middle = -3;
                }else if(colorNum<80){
                    middle = 3;
                }
                title.style.cssText = "color:rgb(255," + colorNum + ", 0)";
            },30);
        }

        //初始化登录界面
        function initLogin() {
            var liArr = document.querySelectorAll(".tab li");
            var divArr = document.querySelectorAll(".page .childpage");

            // 默认显示登录页面
            for (var j = 0; j < divArr.length; j++) {
                divArr[j].style.display = "none";
            }

            if (divArr.length > 0) {
                divArr[0].style.display = "block";
            }

            // 保留登录、注册标签切换
            for (var i = 0; i < liArr.length; i++) {
                liArr[i].index = i;

                liArr[i].onclick = function () {
                    for (var j = 0; j < divArr.length; j++) {
                        liArr[j].style.cssText =
                            "background-color:rgba(255, 255, 255, 0.2);";
                        divArr[j].style.display = "none";
                    }

                    liArr[this.index].style.cssText =
                        "background-color:rgba(255, 255, 255, 0);";

                    divArr[this.index].style.display = "block";
                };
            }

            // 绑定获取邮箱验证码按钮
            $("#SendEmailCode").click(sendEmailCode);
        }

         //登录账号和密码信息验证
         function loginbtn(){
            var user_name = $(".login_page").find("#UserName").val();
            var user_pwd = $(".login_page").find("#PassWord").val();
            var login_error = $(".login_error");
            if((user_name == "") || (user_pwd == "")){
                login_error.text("账号和密码不能为空");
            }
            else{
                login_error.text("");
                $.ajax({
                    type: "post",
                    url: url + "/user/login",
                    data: {
                        user_name: user_name,
                        user_pwd: user_pwd
                    },
                    dataType: "json",
                    success: function(obj){
                        if(obj.msg == "fail"){
                           // sessionStorage.removeItem('userJson');
                            login_error.text('账号或密码错误!');
                        }
                        else{
                            localStorage.setItem("userJson",JSON.stringify(obj.data));
                          // sessionStorage.set
                            window.location.href="./mainPage.jsp";
                        }
                    },
                    error:function(){
                        alert("network error!");
                    }
                });
            }
        }

        function sendEmailCode() {
            var email = $("#Email").val().trim();
            var sendBtn = $("#SendEmailCode");
            if (email === "") {
                $(".register_error").text("请先输入邮箱！");
                return;
            }
            // 简单检查邮箱格式
            var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(email)) {
                $(".register_error").text("请输入正确的邮箱地址！");
                return;
            }
            sendBtn.prop("disabled", true).text("发送中...");
            $(".register_error").text("");
            $.ajax({
                type: "POST",
                url: url + "/user/sendEmailCode",
                data: {
                    email: email
                },
                dataType: "text",
                success: function (data) {
                    data = $.trim(data);
                    if (data === "success") {
                        alert("验证码已发送，请检查邮箱！");
                    } else {
                        $(".register_error").text(
                            data || "验证码发送失败，请稍后重试。"
                        );
                    }
                },
                error: function () {
                    $(".register_error").text(
                        "发送失败，请检查后端接口或网络连接。"
                    );
                },
                complete: function () {
                    sendBtn.prop("disabled", false)
                           .text("获取邮箱验证码");
                }
            });
        }


        function registerbtn() {
            var user_name = $(".register_page").find("#UserName").val().trim();
            var user_pwd = $(".register_page").find("#PassWord").val();
            var user_email = $(".register_page").find("#Email").val().trim();
            var email_code = $("#EmailCode").val().trim();
            var register_error = $(".register_error");

            if (user_name === "" || user_pwd === "" ||
                user_email === "" || email_code === "") {
                register_error.text("请填写完整的注册信息！");
                return;
            }

            if (!/^\d{6}$/.test(email_code)) {
                register_error.text("请输入正确的6位邮箱验证码！");
                return;
            }

            $.ajax({
                type: "POST",
                url: url + "/user/register",
                data: {
                    user_name: user_name,
                    user_pwd: user_pwd,
                    user_email: user_email,
                    email_code: email_code
                },
                dataType: "text",

                success: function (data) {
                    data = $.trim(data);

                    if (data === "success") {
                        alert("注册成功！");
                        window.location.href = "login.jsp";
                    } else if (data === "code_error") {
                        register_error.text("验证码错误或已过期，请重新获取！");
                    } else if (data === "exist" || data === "fail") {
                        register_error.text("注册失败，用户名可能已存在！");
                    } else {
                        register_error.text(data || "注册失败，请稍后重试！");
                    }
                },

                error: function () {
                    register_error.text("注册请求失败，请检查后端接口！");
                }
            });
        }

    </script>
    <!-- ------------------------------------------------------------------- -->
</body>
</html>