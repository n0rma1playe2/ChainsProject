# ChainsProject(还未完成)

一个基于 Jetpack Compose 开发的 Android 电商应用。

## 功能特性

- 商品列表展示
- 商品详情页面
  - 商品基本信息展示
  - 商品图片轮播
  - 商品规格选择
  - 商品评价列表
  - 商品收藏功能
  - 商品分享功能
  - 商品推荐功能
- 购物车功能（开发中）
- 订单功能（开发中）

## 技术栈

- Kotlin
- Jetpack Compose
- MVVM 架构
- Hilt 依赖注入
- Retrofit 网络请求
- Room 本地数据库
- Coil 图片加载

## 开发环境

- Android Studio Hedgehog | 2023.1.1
- Kotlin 1.9.0
- Gradle 8.2
- Android SDK 34
- JDK 17

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/chainsproject/
│   │   │   ├── data/           # 数据层
│   │   │   │   ├── local/      # 本地数据
│   │   │   │   ├── remote/     # 远程数据
│   │   │   │   └── repository/ # 数据仓库
│   │   │   ├── di/            # 依赖注入
│   │   │   ├── domain/        # 领域层
│   │   │   ├── ui/            # 界面层
│   │   │   │   ├── components/ # 可复用组件
│   │   │   │   ├── screens/   # 页面
│   │   │   │   ├── theme/     # 主题
│   │   │   │   └── viewmodels/ # 视图模型
│   │   │   └── utils/         # 工具类
│   │   └── res/               # 资源文件
│   └── test/                  # 测试代码
└── build.gradle              # 应用级构建配置
```

## 开始使用

1. 克隆项目
```bash
git clone https://github.com/yourusername/ChainsProject.git
```

2. 打开项目
- 使用 Android Studio 打开项目
- 等待 Gradle 同步完成

3. 运行项目
- 连接 Android 设备或启动模拟器
- 点击 "Run" 按钮运行项目

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情 
