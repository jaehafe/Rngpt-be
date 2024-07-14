package org.jh.oauthjwt.controller;

import org.jh.oauthjwt.dto.JoinDTO;
import org.jh.oauthjwt.dto.VerifyDTO;
import org.jh.oauthjwt.service.JoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> initiateJoinProcess(@RequestBody JoinDTO joinDTO) {
        String result = joinService.initiateJoinProcess(joinDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyDTO verifyDTO) {
        String result = joinService.verifyEmail(verifyDTO);
        return ResponseEntity.ok(result);
    }
}
