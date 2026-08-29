package andreas.insurance.annotation;

import andreas.insurance.exception.custom.BaseException;
import andreas.insurance.exception.custom.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class BusinessEventAspect {

    @Around("@annotation(annotation)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint, LogBusinessEvent annotation) throws Throwable {

        String eventName = annotation.value();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();

            stopWatch.stop();

            log.info("BUSINESS_EVENT: {}_SUCCESS | Duration: {} ms",
                    eventName, stopWatch.getTotalTimeMillis());

            return result;
        } catch (ResourceNotFoundException ex) {
            stopWatch.stop();

            log.warn("BUSINESS_EVENT: {}_NOT_FOUND | Duration: {} ms | Reason: {}",
                    eventName, stopWatch.getTotalTimeMillis(), ex.getMessage());

            throw ex;
        } catch (BaseException ex) {
            stopWatch.stop();

            log.warn("BUSINESS_EVENT: {}_BUSINESS_RULE_VIOLATION | Duration: {} ms | Reason: {}",
                    eventName, stopWatch.getTotalTimeMillis(), ex.getMessage());

            throw ex;
        } catch (Exception ex) {
            stopWatch.stop();

            log.error("BUSINESS_EVENT: {}_FAILED | Duration: {} ms | Reason: {}",
                    eventName, stopWatch.getTotalTimeMillis(), ex.getMessage(), ex);

            throw ex;
        }

    }

}
