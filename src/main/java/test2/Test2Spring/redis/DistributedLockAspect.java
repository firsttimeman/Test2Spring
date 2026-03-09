package test2.Test2Spring.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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
        String lockName = evaluateKey(joinPoint, lockAnn.key());

        RLock lock = redisson.getLock(lockName);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockAnn.waitTime(), lockAnn.leaseTime(), lockAnn.timeUnit());
            if (!acquired) throw new IllegalStateException("Failed to acquire lock " + lockName);
            return joinPoint.proceed();
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }


    private String evaluateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }

}
