package com.finsight.security.aspect;

import com.finsight.exception.UnauthorizedException;
import com.finsight.security.UserDetailsImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PremiumFeatureAspect {

    @Before("@annotation(com.finsight.security.annotation.PremiumFeature)")
    public void checkPremiumAccess(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (!userDetails.isPremium()) {
                throw new UnauthorizedException("This feature is only available to premium users");
            }
        } else {
            throw new UnauthorizedException("Authentication required");
        }
    }
} 