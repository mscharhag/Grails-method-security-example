package com.mscharhag

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import com.mscharhag.Note;

class GrailsPermissionEvaluator implements PermissionEvaluator {

	def grailsApplication
	def springSecurityService

	@Override
	public boolean hasPermission(Authentication authentication, Object note, Object permission) {
		def user = springSecurityService.getCurrentUser();
		return permission == 'remove' && note.author == user
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		// get domain class with name targetType
		Class domainClass = grailsApplication.getDomainClass(targetType).clazz

		// get domain object with id targetId
		Note note = domainClass.get(targetId)

		return hasPermission(authentication, note, permission)
	}
}