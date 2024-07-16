package org.jh.oauthjwt.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-controller")
    public String testController() {
        return "Test Controller";
    }
}
