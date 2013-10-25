package com.mscharhag

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

class GrailsExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	
	BeanResolver beanResolver

	@Override
	public EvaluationContext createEvaluationContext(Authentication auth, MethodInvocation method) {
		StandardEvaluationContext context = (StandardEvaluationContext) super.createEvaluationContext(auth, method)
		context.setBeanResolver(beanResolver)
		return context
	}
}
