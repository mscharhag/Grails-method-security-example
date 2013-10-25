import com.mscharhag.GrailsBeanResolver;
import com.mscharhag.GrailsExpressionHandler;
import com.mscharhag.GrailsPermissionEvaluator;

// Place your Spring DSL code here
beans = {

	expressionHandler(GrailsExpressionHandler) {
		beanResolver              = ref('beanResolver')
		parameterNameDiscoverer   = ref('parameterNameDiscoverer')
		permissionEvaluator       = ref('permissionEvaluator')
		roleHierarchy             = ref('roleHierarchy')
		trustResolver             = ref('authenticationTrustResolver')
	}

	beanResolver(GrailsBeanResolver) {  
		grailsApplication = ref('grailsApplication')  
	}

	permissionEvaluator(GrailsPermissionEvaluator) {
		grailsApplication     = ref('grailsApplication')
		springSecurityService = ref('springSecurityService')
	}
}
