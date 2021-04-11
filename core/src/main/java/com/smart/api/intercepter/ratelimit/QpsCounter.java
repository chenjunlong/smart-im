package com.smart.api.intercepter.ratelimit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chenjunlong
 * @desc 滑动窗口计数器，单位s
 */
public class QpsCounter {

    private final int bucketNum = 8;
    private List<AtomicLong> counters = new ArrayList<>(this.bucketNum);
    private AtomicLong lastRestIndex = new AtomicLong(System.currentTimeMillis() / 1000);
    private Object mutex = new Object();

    public QpsCounter() {
        for (int i = 0; i < bucketNum; i++) {
            counters.add(new AtomicLong(0L));
        }
    }

    public long incr(long timeInMills, long incrValue) {
        this.resetIfNecessary();
        long timeInSeconds = timeInMills / 1000;
        int index = (int) (timeInSeconds & (bucketNum - 1));
        return counters.get(index).addAndGet(incrValue);
    }

    public long get(long timeInMills) {
        this.resetIfNecessary();
        long timeInSeconds = timeInMills / 1000;
        int index = (int) (timeInSeconds & (bucketNum - 1));
        return counters.get(index).get();
    }

    private void resetIfNecessary() {
        long nowSecond = System.currentTimeMillis() / 1000;
        long lastRestIndexValue = lastRestIndex.get();
        if (nowSecond - lastRestIndexValue > 1) {
            long from = lastRestIndexValue;
            long to = nowSecond - 1;
            long delta = to - from;
            if (delta > 0) {
                // 当reset动作回环到当前点nowSecond时，需要加锁避免数据冲突
                if (nowSecond - from >= this.bucketNum - 1) {
                    synchronized (this.mutex) {
                        reset(lastRestIndexValue, from, to);
                    }
                } else {
                    reset(lastRestIndexValue, from, to);
                }
            }
        }
    }

    private void reset(long lastResetIndexValue, long from, long to) {
        if (lastRestIndex.compareAndSet(lastResetIndexValue, to)) {
            long realTo = Math.min(to, from + this.bucketNum);
            for (long i = from; i <= realTo; i++) {
                int index = (int) (i & (bucketNum - 1));
                counters.get(index).set(0L);
            }
        }
    }

}
