package com.dfec.soft.secret.common.service.impl;

import com.dfec.soft.secret.common.service.UidService;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import org.springframework.stereotype.Service;

/**
 * UUID v7 实现，使用 {@code java-uuid-generator} 生成时间有序的 32 位唯一 ID。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class Uuidv7UidServiceImpl implements UidService {

    private static final NoArgGenerator GENERATOR = Generators.timeBasedEpochGenerator();

    @Override
    public String next() {
        return GENERATOR.generate().toString().replace("-", "");
    }
}
