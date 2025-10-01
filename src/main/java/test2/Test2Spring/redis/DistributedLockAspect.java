package test2.Test2Spring.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)

public class DistributedLockAspect {

    private final RedissonClient redisson;

    public DistributedLockAspect(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Around("@annotation(lockAnn)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributeLock lockAnn) throws Throwable {
        String lockName = lockAnn.key();

        RLock lock = redisson.getLock(lockName);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockAnn.waitTime(), lockAnn.leaseTime(), lockAnn.timeUnit());
            if(!acquired) {
                throw new IllegalStateException("Failed to acquire lock" + lockName);
            }
            return joinPoint.proceed();
        } finally {
            if(acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
