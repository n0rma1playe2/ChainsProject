# ChainsProject

基于 Jetpack Compose 的 Android 应用项目

## 项目说明

这是一个使用现代 Android 开发技术栈构建的应用项目。

### 技术栈

- Kotlin 作为主要开发语言
- Jetpack Compose 用于 UI 开发
- MVVM 架构模式
- Material Design 3 设计规范
- Room 数据库
- Hilt 依赖注入
- Retrofit 网络请求
- Kotlin Coroutines & Flow

### 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/chainsproject/
│   │   │   ├── data/           # 数据层
│   │   │   ├── di/            # 依赖注入
│   │   │   ├── domain/        # 领域层
│   │   │   ├── ui/            # UI 层
│   │   │   └── utils/         # 工具类
│   │   └── res/               # 资源文件
│   └── test/                  # 测试代码
└── build.gradle.kts          # 项目构建配置
```

### 开发环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17 或更高版本
- Android SDK 34 或更高版本
- Gradle 8.2 或更高版本

### 构建和运行

1. 克隆项目
2. 在 Android Studio 中打开项目
3. 等待 Gradle 同步完成
4. 点击运行按钮或使用快捷键 Shift + F10

### 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

### 许可证

MIT License 