package com.mscharhag

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

class GrailsBeanResolver implements BeanResolver {
	
	GrailsApplication grailsApplication

	@Override
	public Object resolve(EvaluationContext evaluationContext, String beanName) throws AccessException {
		return grailsApplication.mainContext.getBean(beanName)
	}
}
