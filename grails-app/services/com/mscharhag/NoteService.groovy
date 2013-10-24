package com.mscharhag

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

class NoteService {
	
	def springSecurityService
	
	
	@PreAuthorize('permitAll()')
	public long getTotalNoteCount() {
		return Note.count()
	}
	
	
	@PreAuthorize('isFullyAuthenticated()')
	public void createNote(Note note) {
		Note newNote = new Note(title: note.title, text: note.text)		
		User user = springSecurityService.getCurrentUser()
		user.addToNotes(newNote)
		user.save(failOnError: true)
	}
	
	
	@PostAuthorize('isAuthenticated() and principal.username == returnObject.author.username')
	public Note getNote(long id) {
		return Note.get(id)
	}
	
	
	@PreAuthorize('isAuthenticated() and principal.username == #note.author.username')
	public void updateNote(Note note) {
		note.save(failOnError: true)
	}
	

	

	@PreAuthorize("@securityService.canRemoveNote(#id)")
	public void removeNoteUsingBeanResolver(long id) {
		Note note = getNote(id)
		User user = note.author
		user.removeFromNotes(note)
		user.save(failOnError: true)
	}
	
	
	@PreAuthorize("hasPermission(#id, 'com.mscharhag.Note', 'remove')")
	public void removeNoteUsingPermissionEvaluator(long id) {
		Note note = getNote(id)
		User user = note.author
		user.removeFromNotes(note)
		user.save(failOnError: true)
	}
}
