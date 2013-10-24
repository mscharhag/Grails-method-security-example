package com.mscharhag

class SecurityService {
	
	def springSecurityService
	
	public boolean canRemoveNote(long id) {
		Note note = Note.get(id)
		return note.author == springSecurityService.getCurrentUser()
	}
	
}
