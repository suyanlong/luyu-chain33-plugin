![](doc/img/logo_nor.svg)

# chain33陆羽跨链协议插件

luyu-chain33-plugin是[chain33](https://github.com/33cn)用于适配[陆羽跨链协议](https://gitee.com/luyu-community/luyu-cross-chain-protocol)的插件。

## 编译插件

**环境要求**:

- [JDK8及以上](https://www.oracle.com/java/technologies/javase-downloads.html)
- scala 2.12.8及以上
- sbt 1.5.5及以上

**编译命令**:
```shell
make build
```

**打包命令**:

```shell
make assembly
```

## 插件使用

该插件要配合[陆羽协议路由](https://gitee.com/luyu-community/router)一起使用。


#### 启动链


#### 路由配置
路由需要配置文件提供一些链的信息。


#### 停止链

## [chain33社区](https://github.com/33cn/chain33)

## 目录结构

```shell
├── lib                 #外部依赖苦
├── project
│   ├── project   #项目依赖、插件等管理文件
├── scripts             #常用脚步文件
├── src                 #源码目录
│   ├── main      #主要文件目录
│   ├── resources #配置文件目录
│   └── test      #测试文件目录


```

## [开发指南](./doc/develop.md)

## Q&A
正在开发中



