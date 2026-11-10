package com.neurosoft.employee_management.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.neurosoft.employee_management.attendance.repository",
        entityManagerFactoryRef = "attendanceEntityManagerFactory",
        transactionManagerRef = "attendanceTransactionManager"
)
public class AttendanceDataSourceConfig {

    @Bean(name = "attendanceDataSource")
    public DataSource attendanceDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5433/attendance_db");
        ds.setUsername("admin");
        ds.setPassword("admin123");
        ds.setDriverClassName("org.postgresql.Driver");
        System.out.println(">>> Attendance DS URL: " + ds.getJdbcUrl());
        System.out.println(">>> Attendance DS user: " + ds.getUsername());
        return ds;
    }

    @Bean(name = "attendanceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean attendanceEntityManagerFactory(
            @Qualifier("attendanceDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.neurosoft.employee_management.attendance.entity");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(adapter);

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "true");
        em.setJpaPropertyMap(props);

        return em;
    }

    @Bean(name = "attendanceTransactionManager")
    public PlatformTransactionManager attendanceTransactionManager(
            @Qualifier("attendanceEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}