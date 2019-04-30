# framework-core
Android框架核心模块，包含：

* 1.注解模块
* 2.抽象业务模块

## 一.Application

<img src="https://github.com/xqy666666/Framework-core/blob/master/Application.png"  alt="application"/>

> 调用AndroidApplication.run()方法会初始化网络请求模块、注解相关代码、协议类和UI的绑定

## 二.Repository

## 图一

<img src="https://github.com/xqy666666/Framework-core/blob/master/repository.png"  alt="repository"/>

## 图二

> 继承DataBaseCallback

<img src="https://github.com/xqy666666/Framework-core/blob/master/repository2.png"  alt="repository2"/>

> 实际获取数据相关类。图一是单纯的从网络获取数据；图二是对数据保存更新的操作 

## 三.Contract(逻辑协议类)

<img src="https://github.com/xqy666666/Framework-core/blob/master/contract.png"  alt="repository2"/>

> Contract注解中的参数表示要绑定到的Activity或Fragment。Presenter相关类不需要初始化，会自动绑定到Activity和Fragment的生命周期。在Presenter中可通过调用mView.request()进行网络请求。第一个参数表示你要调用的Api请求。必须是Service注解所绑定的参数。

## 四.Activity或Fragment实现Contract协议类中AppView的子接口

<img src="https://github.com/xqy666666/Framework-core/blob/master/mainActivity.png"  alt="Activity"/>

