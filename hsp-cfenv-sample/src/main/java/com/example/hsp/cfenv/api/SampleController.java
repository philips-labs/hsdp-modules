package com.example.hsp.cfenv.api;

import com.example.hsp.cfenv.data.PgRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class SampleController {

    @Value("${spring.application.name}")
    String appName;
    @Value("${spring.application.version}")
    String appVer;
    @Value("${spring.application.description}")
    String appDesc;

    @Autowired
    PgRepository pgRepository;

    @GetMapping("/version")
    public Version getVersion() {
        log.info("API Version called");
        return new Version(appName, appDesc, appVer,
                "build-11012022-1.0.0.0", "Client-Test", "1");
    }

    @GetMapping("/pgInfo")
    public Version getPgInfo() {
        log.info("API pgInfo called");
        String serverVer = pgRepository.getServerVer();
        return new Version("Postgres RDS", "PostgreSQL information", serverVer,
                "Unknown", "Client-Test", "1");
    }

}
