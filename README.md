### 项目介绍 ###
一个简洁的im通讯系统；

![核心流转](./docs/images/smart-im.png)


#### 项目结构 ####
```
smart-im
 ├── api 网关接口
 ├── core 核心组件
 ├── service 业务处理
 └── tcpserver 长连接通信服务
```


#### 接口列表 ####
|名称   | url  |
|:-----|:-----|
| 获取tcp服务地址 | /v1/smart-im/dispatch/connect_address |
| 投递IM消息| /v1/smart-im/message/send |
| 获取运行数据| /v1/smart-im/data/summary |


#### 开发组件 ####
1. 参数校验：ParamDesc中range支持 int、long、float、double、string
2. 异常框架：接口增加debug=true参数可以返回，抛异常时写入的附加参数
3. 限流组件：QpsCounter采用滑动窗口算法实现；通过包扫描初始化，经过Interceptor进行校验


#### 待开发部分 ####
1. 在线数据：TcpServer在线人数上报；在线数据获取接口；进出场事件消息；
2. 限流组件：api接口限流（已完成）；tcp建联限流；
3. 服务发现：TcpServer服务注册发现机制从redis升级为nacos；
4. 监控采集：采集系统qps、rt、tcp_connections等数据上报到Prometheus
