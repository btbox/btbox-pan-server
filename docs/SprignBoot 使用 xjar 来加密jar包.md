# 1.介绍 xjar
### pring Boot JAR 安全加密运行工具, 同时支持的原生JAR.

### [基于对JAR包内资源的加密以及拓展ClassLoader来构建的一套程序加密启动, 动态解密运行的方案, 避免源码泄露以及反编译.](https://github.com/core-lib/xjar#%E5%9F%BA%E4%BA%8E%E5%AF%B9jar%E5%8C%85%E5%86%85%E8%B5%84%E6%BA%90%E7%9A%84%E5%8A%A0%E5%AF%86%E4%BB%A5%E5%8F%8A%E6%8B%93%E5%B1%95classloader%E6%9D%A5%E6%9E%84%E5%BB%BA%E7%9A%84%E4%B8%80%E5%A5%97%E7%A8%8B%E5%BA%8F%E5%8A%A0%E5%AF%86%E5%90%AF%E5%8A%A8-%E5%8A%A8%E6%80%81%E8%A7%A3%E5%AF%86%E8%BF%90%E8%A1%8C%E7%9A%84%E6%96%B9%E6%A1%88-%E9%81%BF%E5%85%8D%E6%BA%90%E7%A0%81%E6%B3%84%E9%9C%B2%E4%BB%A5%E5%8F%8A%E5%8F%8D%E7%BC%96%E8%AF%91)

## [功能特性](https://github.com/core-lib/xjar#%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7)

-   无代码侵入, 只需要把编译好的JAR包通过工具加密即可.
-   完全内存解密, 降低源码以及字节码泄露或反编译的风险.
-   支持所有JDK内置加解密算法.
-   可选择需要加解密的字节码或其他资源文件.
-   支持Maven插件, 加密更加便捷.
-   动态生成Go启动器, 保护密码不泄露.




# 2. 添加依赖
- pom.xml 文件添加
```xml
<project>
    <!-- 设置 jitpack.io 仓库 -->
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    <!-- 添加 XJar 依赖 -->
    <dependencies>
        <dependency>
            <groupId>com.github.core-lib</groupId>
            <artifactId>xjar</artifactId>
            <version>4.0.2</version>
            <!-- <scope>test</scope> -->
        </dependency>
    </dependencies>
</project>
```



# 3. 参数解释

| 方法名称 | 参数列表                                                     | 是否必选                             | 方法说明                                        |
| -------- | ------------------------------------------------------------ | ------------------------------------ | ----------------------------------------------- |
| from     | (String jar)                                                 | 二选一                               | 指定待加密JAR包路径                             |
| from     | (File jar)                                                   | 指定待加密JAR包文件                  |                                                 |
| use      | (String password)                                            | 二选一                               | 指定加密密码                                    |
| use      | (String algorithm, int keysize, int ivsize, String password) | 指定加密算法及加密密码               |                                                 |
| include  | (String ant)                                                 | 可多次调用                           | 指定要加密的资源相对于classpath的ANT路径表达式  |
| include  | (Pattern regex)                                              | 可多次调用                           | 指定要加密的资源相对于classpath的正则路径表达式 |
| exclude  | (String ant)                                                 | 可多次调用                           | 指定不加密的资源相对于classpath的ANT路径表达式  |
| exclude  | (Pattern regex)                                              | 可多次调用                           | 指定不加密的资源相对于classpath的正则路径表达式 |
| to       | (String xJar)                                                | 二选一                               | 指定加密后JAR包输出路径, 并执行加密.            |
| to       | (File xJar)                                                  | 指定加密后JAR包输出文件, 并执行加密. |                                                 |

- 指定加密算法的时候密钥长度以及向量长度必须在算法可支持范围内, 具体加密算法的密钥及向量长度请自行百度或谷歌.
- include 和 exclude 同时使用时即加密在include的范围内且排除了exclude的资源.



# 4. 加密方式

## 一、main方法

```java
public class Test {
	public static void main(String[] args) {  
	    XCryptos.encryption()
	            .from("/path/to/read/plaintext.jar")  
	            .use("io.xjar")  
	            .include("/io/xjar/**/*.class")  
	            .include("/mapper/**/*Mapper.xml")  
	            .exclude("/static/**/*")  
	            .exclude("/conf/*")  
	            .to("/path/to/save/encrypted.jar");  
	}
}
```

## 二、maven 插件

```xml
<project>
    <!-- 设置 jitpack.io 插件仓库 -->
    <pluginRepositories>
        <pluginRepository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </pluginRepository>
    </pluginRepositories>
    <!-- 添加 XJar Maven 插件 -->
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.core-lib</groupId>
                <artifactId>xjar-maven-plugin</artifactId>
                <version>4.0.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                        </goals>
                        <phase>package</phase>
                        <!-- 或使用
                        <phase>install</phase>
                        -->
                        <configuration>
                            <!-- optional
                            <algorithm/>
                            <keySize/>
                            <ivSize/>
                            <includes>
                                <include/>
                            </includes>
                            <excludes>
                                <exclude/>
                            </excludes>
                            <sourceDir/>
                            <sourceJar/>
                            <targetDir/>
                            <targetJar/>
                            -->
                            <!-- 生成加密的jar文件路径 -->
                            <targetDir>test.jar</targetDir>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

```
# 方式一 password是密码
mvn clean package -Dxjar.password=io.xjar
# 方式二 Dxjar.targetDir 指定的是 go 的解密文件路径存放地方
mvn clean install -Dxjar.password=io.xjar -Dxjar.targetDir=/directory/to/save/target.xjar
```

### 参数说明

| 参数名称  | 命令参数名称     | 参数说明                 | 参数类型 | 缺省值                          | 示例值                                                       |
| --------- | ---------------- | ------------------------ | -------- | ------------------------------- | ------------------------------------------------------------ |
| password  | -Dxjar.password  | 密码字符串               | String   | 必须                            | 任意字符串, io.xjar                                          |
| algorithm | -Dxjar.algorithm | 加密算法名称             | String   | AES/CBC/PKCS5Padding            | JDK内置加密算法, 如：AES/CBC/PKCS5Padding 和 DES/CBC/PKCS5Padding |
| keySize   | -Dxjar.keySize   | 密钥长度                 | int      | 128                             | 根据加密算法而定, 56, 128, 256                               |
| ivSize    | -Dxjar.ivSize    | 密钥向量长度             | int      | 128                             | 根据加密算法而定, 128                                        |
| sourceDir | -Dxjar.sourceDir | 源jar所在目录            | File     | ${project.build.directory}      | 文件目录                                                     |
| sourceJar | -Dxjar.sourceJar | 源jar名称                | String   | ${project.build.finalName}.jar  | 文件名称                                                     |
| targetDir | -Dxjar.targetDir | 目标jar存放目录          | File     | ${project.build.directory}      | 文件目录                                                     |
| targetJar | -Dxjar.targetJar | 目标jar名称              | String   | ${project.build.finalName}.xjar | 文件名称                                                     |
| includes  | -Dxjar.includes  | 需要加密的资源路径表达式 | String[] | 无                              | io/xjar/** , mapper/*Mapper.xml , 支持Ant表达式              |
| excludes  | -Dxjar.excludes  | 无需加密的资源路径表达式 | String[] | 无                              | static/** , META-INF/resources/** , 支持Ant表达式            |

- 指定加密算法的时候密钥长度以及向量长度必须在算法可支持范围内, 具体加密算法的密钥及向量长度请自行百度或谷歌.
- 当 includes 和 excludes 同时使用时即加密在includes的范围内且排除了excludes的资源.

- 加密后的效果



## 5.编译脚本

```go
go build xjar.go
```

-   通过步骤2加密成功后XJar会在输出的JAR包同目录下生成一个名为 xjar.go 的的Go启动器源码文件.
-   将 xjar.go 在不同的平台进行编译即可得到不同平台的启动器可执行文件, 其中Windows下文件名为 xjar.exe 而Linux下为 xjar.
-   用于编译的机器需要安装 Go 环境, 用于运行的机器则可不必安装 Go 环境, 具体安装教程请自行搜索.
-   由于启动器自带JAR包防篡改校验, 故启动器无法通用, 即便密码相同也不行.



# 6.运行加密的jar

- xjar 为 上面 编译后的文件

- OPTIONS 是你的 jar 运行参数

- 三种方式运行
```shell
/path/to/xjar /path/to/java [OPTIONS] -jar /path/to/encrypted.jar

/path/to/xjar /path/to/javaw [OPTIONS] -jar /path/to/encrypted.jar

nohup /path/to/xjar /path/to/java [OPTIONS] -jar /path/to/encrypted.jar
```



# 7. 注意事项

#### 1. 不兼容 spring-boot-maven-plugin 的 executable = true 以及 embeddedLaunchScript

<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <!-- 需要将executable和embeddedLaunchScript参数删除, 目前还不能支持对该模式Jar的加密！后面可能会支持该方式的打包. 
    <configuration>
        <executable>true</executable>
        <embeddedLaunchScript>...</embeddedLaunchScript>
    </configuration>
    -->
</plugin>

#### 2. Spring Boot + JPA(Hibernate) 启动报错问题

如果项目中使用了 JPA 且实现为Hibernate时, 由于Hibernate自己解析加密后的Jar文件, 所以无法正常启动, 可以采用以下解决方案:

1.  clone [XJar-Agent-Hibernate](https://github.com/core-lib/xjar-agent-hibernate) , 使用 mvn clean package 编译出 xjar-agent-hibernate-${version}.jar 文件
2.  采用 xjar java -javaagent:xjar-agent-hibernate-${version}.jar -jar your-spring-boot-app.jar 命令启动

3. 静态文件浏览器无法加载完成问题

由于静态文件被加密后文件体积变大, Spring Boot 会采用文件的大小作为 Content-Length 头返回给浏览器, 但实际上通过 XJar 加载解密后文件大小恢复了原本的大小, 所以浏览器认为还没接收完导致一直等待服务端. 由此我们需要在加密时忽略静态文件的加密, 实际上静态文件也没加密的必要, 因为即便加密了用户在浏览器 查看源代码也是能看到完整的源码.通常情况下静态文件都会放在 static/ 和 META-INF/resources/ 目录下, 我们只需要在加密时通过 exclude 方法排除这些资源即可, 可以参考以下例子：

XCryptos.encryption()
        .from("/path/to/read/plaintext.jar")
        .use("io.xjar")
        .exclude("/static/**/*")
        .exclude("/META-INF/resources/**/*")
        .to("/path/to/save/encrypted.jar");

#### 4. JDK-9及以上版本由于模块化导致XJar无法使用 jdk.internal.loader 包的问题解决方案

在启动时添加参数 --add-opens java.base/jdk.internal.loader=ALL-UNNAMED

xjar java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED -jar /path/to/encrypted.jar

#### 5. 由于使用了阿里云Maven镜像导致无法从 jitpack.io 下载 XJar 依赖的问题

参考如下设置, 在镜像配置的 mirrorOf 元素中加入 ,!jitpack.io 结尾.

```xml
<mirror>
    <id>alimaven</id>
    <mirrorOf>central,!jitpack.io</mirrorOf>
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
</mirror>
```



#### 6. springboot3+jdk17问题

- 出现问题：Exception in thread "main" java.lang.reflect.InaccessibleObjectException: Unable to make field private final jdk.internal.loader.URLClassPath java.net.URLClassLoader.ucp accessible: module java.base does not "opens java.net" to unnamed module @649d209a
- 使用如下参数解决

```
xjar java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED -jar encrypt20231114114720.jar
```

