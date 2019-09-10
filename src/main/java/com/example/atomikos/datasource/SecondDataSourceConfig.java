package com.example.atomikos.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Administrator
 */
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

//        //mybatis通用配置
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        //自动驼峰配置
//        configuration.setMapUnderscoreToCamelCase(true);
//        //sql日志打印配置
//        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
//        bean.setConfiguration(configuration);
        return bean.getObject();
    }

//    @Bean(name = "secondeTransactionManager")
//    public DataSourceTransactionManager secondeTransactionManager(@Qualifier("secondeDataSource") DataSource dataSource) {
//        SecondDataSourceConfig.logDs(dataSource);
//        return new DataSourceTransactionManager(dataSource);
//    }

//    @Bean(name = "secondeSqlSessionTemplate")
//    public SqlSessionTemplate secondeSqlSessionTemplate(
//            @Qualifier("secondeSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
//        return template;
//    }
//
//
//    /**
//     * 显示数据库连接池信息
//     *
//     * @param dataSource
//     */
//    public static void logDs(DataSource dataSource) {
//        HikariDataSource hds = (HikariDataSource) dataSource;
//        String info = "\n\n\tHikariCP连接池配置\n\t连接池名称：" +
//                hds.getPoolName() +
//                "\n\t最小空闲连接数：" +
//                hds.getMinimumIdle() +
//                "\n\t最大连接数：" +
//                hds.getMaximumPoolSize() +
//                "\n\t连接超时时间：" +
//                hds.getConnectionTimeout() +
//                "ms\n\t空闲连接超时时间：" +
//                hds.getIdleTimeout() +
//                "ms\n\t连接最长生命周期：" +
//                hds.getMaxLifetime() +
//                "ms\n";
//        log.info(info);
//    }

}
