# zmall-be
zmall电商后端接口项目

[zmall电商网站线上地址][1]

[zmall电商网站接口文档地址][2]
# 环境参数
```
操作系统：Centos 6.8            jdk版本:7u80
项目管理：git2.8、Maven3.0.5    IDE:idea 2017
技术栈：SSM/Redis/jQuery        服务器软件:vsftpd、nginx、tomcat
模块化方案：CommonJs+WebPack    数据库：Mysql、Redis
架构模式:前后端完全分离、分层架构模块化、后端提供ResutFul风格的API接口
项目简介：
完成前后端分离的电商项目开发并上线部署。
主要技术点：
-前后端通信时, 统一的高复用服务响应对象的封装
-接口异常时，利用Spring MVC的HandlerExceptionResolver进行统一异常处理
-通过Nginx负载均衡完成Tomcat集群
-通过Spring-session解决集群环境下session共享问题
-通过Spring-schedule与Redis分布式锁完成集群环境下定时任务编写
-解决用户模块出现的横向越权、纵向越权安全漏洞
-使用BigDemical解决商业计算中浮点数精度丢失问题
-商品分类无限次层级树结构的设计和递归算法的设计
-HashMap完成复杂对象的排重
-MyBatis PageHelper动态分页与排序
-完成与FTP服务对接，商品图片上传，vsftpd服务配置，完成业务服务与数据服务的
分离
-支付宝当面付产品在系统中的集成
-支付宝回调函数编写，回调的正确性的验证，支付宝重复通知的过滤
```
# 项目初始化
 1. 使用resource文件夹下的zmall.sql完成数据库初始化
 2. 修改resource.dev文件夹的datasource.properties的为本机的MySql配置
 3. 把WEB-INF/lib下的支付宝sdk的jar包添加到项目中
 4. 配置tomcat，启动项目

  [1]: http://www.zwtzmall.cn
  [2]: https://github.com/zweitian/zmall-be/wiki