package com.mscharhag
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken

class NoteServiceTests extends GroovyTestCase {
	
	def springSecurityService
	def noteService
	
	def noteId
	
	
	@Before
	void before() {
		setupTestData();
		loginAsAnonymous()
	}
	
	
	void testGetTotalNoteCountAnonymous() { // A not logged in user CAN retrieve the total note count
		long noteCount = noteService.getTotalNoteCount()
		assert noteCount == 1
	}
	
	
	void testGetTotalNoteCountLoggedIn() { // A logged in user CAN retrieve the total note count
		loginAs('paul')
		long noteCount = noteService.getTotalNoteCount()
		assert noteCount == 1
	}

	
	void testCreateNoteAnonymous() { // A not logged in user CAN NOT create a new note
		shouldFail(AccessDeniedException) {
			noteService.createNote(new Note(title: 'foo', text: 'bar' ))
		}
	}
	
	
	void testCreateNoteLoggedIn() { // A logged in user CAN create a new note
		loginAs('paul')
		noteService.createNote(new Note(title: 'foo', text: 'bar' ))
		
		Note note = Note.findByTitle('foo')
		assert note.text 	== 'bar'
	}
	
	
	void testGetNoteAnonymous() { // A not logged in user CAN NOT get a specific note
		shouldFail(AccessDeniedException) {
			noteService.getNote(noteId)
		}
	}
	
	
	void testGetNoteLoggedIn() { // A logged in user CAN NOT get a note created by another user
		loginAs('paul')
		shouldFail(AccessDeniedException) {
			noteService.getNote(noteId)
		}
	}
	
	
	void testGetNoteAuthor() { // The author CAN get his own note
		loginAs('john')
		Note note = noteService.getNote(noteId)
		assert note.title == "John's note"
	}
	
	
	void testUpdateAnonymous() { // A not logged in user CAN NOT update a note
		Note note = Note.get(noteId)
		note.title = 'new title'
		
		shouldFail(AccessDeniedException) {
			noteService.updateNote(note)
		}
	}
	
	
	void testUpdateLoggedIn() { // A logged in user CAN NOT update a note created by another user
		loginAs('paul')
		Note note = Note.get(noteId)
		note.title = 'new title'
		
		shouldFail(AccessDeniedException) {
			noteService.updateNote(note)
		}
	}
	
	
	void testUpdateAuthor() { // The author CAN update his own note
		loginAs('john')
		Note note = Note.get(noteId)
		note.title = 'new title'
		
		noteService.updateNote(note)
		
		Note dbNote = Note.get(noteId)
		assert dbNote.title == 'new title'
	}

	
	void testRemoveNoteUsingBeanResolverAnonymous() { // A not logged in user CAN NOT remove a note
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingBeanResolver(noteId)
		}
	}
	
	
	void testRemoveNoteUsingBeanResolverLoggedIn() { // A logged in user CAN NOT remove a note created by another user
		loginAs('paul')
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingBeanResolver(noteId)
		}
	}
	
	
	void testRemoveNoteUsingBeanResolverAuthor() { // The author of the note CAN remove his own note
		loginAs('john')
		noteService.removeNoteUsingBeanResolver(noteId)
		assert Note.count() == 0
	}

	
	void testRemoveNoteUsingPermissionEvaluatorAnonymous() { // A not logged in user CAN NOT remove a note
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingPermissionEvaluator(noteId)
		}
	}
	
	
	void testRemoveNoteUsingPermissionEvaluatorLoggedIn() { // A logged in user CAN NOT remove a note created by another user
		loginAs('paul')
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingPermissionEvaluator(noteId)
		}
	}
	
	
	void testRemoveNoteUsingPermissionEvaluatorAuthor() { // The author of the note CAN remove his own note
		loginAs('john')
		noteService.removeNoteUsingPermissionEvaluator(noteId)
		assert Note.count() == 0
	}
	
	
	private void setupTestData() {
		
		// Create two users: john and paul
		User john = new User(username: 'john', password: 'secr3t', enabled: true).save(failOnError: true)
		User paul = new User(username: 'paul', password: 's3cret', enabled: true).save(failOnError: true)

		// Give both users the role ROLE_USER 
		// Roles are not used in this example but Spring Security requires at least one role per user
		Role role = new Role(authority: 'ROLE_USER').save(failOnError: true)
		UserRole.create(john, role)
		UserRole.create(paul, role)
		
		// Add a note to john
		Note note = new Note(title: "John's note", text: 'some text')
		john.addToNotes(note)
		john.save(flush: true, failOnError: true)
		
		noteId = note.id
		
		assert User.count() == 2
		assert Note.count() == 1
	}
	
	
	private void loginAs(String username) {
		springSecurityService.reauthenticate(username)
	}
	
	
	private void loginAsAnonymous() {
		SCH.context.setAuthentication(new AnonymousAuthenticationToken('key', 'Anonymous', AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")))
	}
}
