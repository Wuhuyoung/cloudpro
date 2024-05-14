package com.cloud.pro.bloom.filter.local;

import com.cloud.pro.bloom.filter.core.BloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@SpringBootApplication
@Slf4j
public class BloomFilterTest {
    @Resource
    private LocalBloomFilterManager localBloomFilterManager;

    @Test
    public void testLocalBloomFilter() {
        BloomFilter<Integer> bloomFilter = localBloomFilterManager.getFilter("test");
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i);
        }
        int failNum = 0;
        for (int i = 1000000; i < 1100000; i++) {
            boolean mightContain = bloomFilter.mightContain(i);
            if (mightContain) {
                failNum++;
            }
        }
        log.info("test num:{}, fail num:{}, fail rate:{}", 100000, failNum, (double) failNum / 100000);
    }
}
