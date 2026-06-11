package com.dfec.soft.secret.common.controller;

import com.dfec.soft.secret.common.dto.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器（内置，不可删除）。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
public class HealthController {

    /**
     * 健康检查。
     *
     * @return OK
     */
    @GetMapping("/health")
    public R<String> checkHealth() {
        return R.ok("OK");
    }
}
