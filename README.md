

# springboot集成atomikos实现分布式事务

### 1、引入基础依赖
```xml
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jta-atomikos</artifactId>
            <version>2.1.8.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.5</version>
        </dependency>
```

### 2、配置数据源

```yml
spring:
  jta:
    enabled: true
    atomikos:
      properties:
        service: com.atomikos.icatch.standalone.UserTransactionServiceFactory
        max-actives: 200
        # 关闭atomikos日志文件输出
        enable-logging: true
        log-base-dir: translogs  #事务日志配置
        log-base-name: tmlog
      datasource:
        first:
          unique-resource-name: first
          xa-data-source-class-name: com.mysql.cj.jdbc.MysqlXADataSource
          poolSize: 5
          minPoolSize: 3
          maxPoolSize: 20
          borrowConnectionTimeout: 60
          reapTimeout: 300
          maxIdleTime: 60
          maintenanceInterval: 60
          loginTimeout: 60
          testQuery: select 1
          xa-properties:
            url: jdbc:mysql://192.168.17.10:3306/user?useUnicode=true&characterEncoding=UTF-8&useSSL=false&rewriteBatchedStatements=true&useLocalSessionState=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai
            user: root
            password: root123
        second:
          unique-resource-name: second
          xa-data-source-class-name: com.mysql.cj.jdbc.MysqlXADataSource
          poolSize: 5
          minPoolSize: 3
          maxPoolSize: 20
          borrowConnectionTimeout: 60
          reapTimeout: 300
          maxIdleTime: 60
          maintenanceInterval: 60
          loginTimeout: 60
          testQuery: select 1
          xa-properties:
            url: jdbc:mysql://192.168.17.10:3307/user_log?useUnicode=true&characterEncoding=UTF-8&useSSL=false&rewriteBatchedStatements=true&useLocalSessionState=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai
            user: root
            password: root123
```

其中，事务日志配置必须开启，保证atomikos在事务未处理完毕时应用死掉，再次重启应用继续事务处理。

### 3、配置集成mybatis

```java
@Slf4j
@Configuration
@MapperScan(basePackages = {
        "com.example.atomikos.dao.first"}, sqlSessionFactoryRef = "firstSqlSessionFactory")
public class FirstDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.jta.atomikos.datasource.first")
    public DataSource firstDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean
    @Primary
    public SqlSessionFactory firstSqlSessionFactory(@Qualifier("firstDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
```

```java
@Slf4j
@Configuration
@MapperScan(basePackages = {
        "com.example.atomikos.dao.second"}, sqlSessionFactoryRef = "secondeSqlSessionFactory")
public class SecondDataSourceConfig {


    @Bean
    @ConfigurationProperties("spring.jta.atomikos.datasource.second")
    public DataSource secondeDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "secondeSqlSessionFactory")
    public SqlSessionFactory secondeSqlSessionFactory(@Qualifier("secondeDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
```

### 4、使用

```java
@RestController
@Slf4j
public class TestController {

    @Autowired
    private FirstDao firstDao;

    @Autowired
    private SecondDao secondDao;

    @GetMapping("/testInsert")
    @Transactional
    public Integer testInsert() {
        HashMap<String, Object> hashMap = new HashMap<>();
        String userId = UUID.randomUUID().toString().replaceAll("-", "");
        hashMap.put("userName", userId);
        Integer user = firstDao.insertUser(hashMap);
//        int a = 1 / 0; //模拟异常
        secondDao.insertLog(hashMap.get("userId").toString(), "测试插入用户" + userId);
        return user;
    }

}
```

直接在方法上加入@Transactional注解

### 5、其它

##### 1、atomikos调试日志开启配置

```yml
logging:
  level:
    com.atomikos: debug  #单独针对atomikos控制日志级别
```

##### 2、关于atomikos两段提交

二阶段提交的算法思路可以概括为：协调者询问参与者是否准备好了提交，并根据所有参与者的反馈情况决定向所有参与者发送commit或者rollback指令（协调者向所有参与者发送相同的指令）。