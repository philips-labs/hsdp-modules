package com.example.hsp.cfenv.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PgInfoRepository implements PgRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String getServerVer() {
        return jdbcTemplate.queryForObject("SELECT version();", String.class);
    }

    @Override
    public String getTimeStamp() {
        return jdbcTemplate.queryForObject("SELECT CURRENT_TIMESTAMP", String.class);
    }
}
