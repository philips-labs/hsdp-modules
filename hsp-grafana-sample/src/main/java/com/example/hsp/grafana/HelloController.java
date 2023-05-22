package com.example.hsp.grafana;

import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class HelloController {

    @GetMapping("/api/version")
    @Timed(value = "version.time", description = "Response time for version API(ms)")
    public VersionInfo getVersion() throws InterruptedException {
        Random random = new Random();
        Thread.sleep(random.nextInt(250-50) + 50);
        return new VersionInfo("HSPGrafanaSample", "v1.0.0");
    }
}
