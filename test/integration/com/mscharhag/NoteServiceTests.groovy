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
	
	def id
	
	@Before
	void before() {
		User john = new User(username: 'john', password: 'secr3t', enabled: true).save(failOnError: true)
		User paul = new User(username: 'paul', password: 's3cret', enabled: true).save(failOnError: true)
		
		Role role = new Role(authority: 'ROLE_USER').save(failOnError: true)
		
		UserRole.create(john, role)
		UserRole.create(paul, role)
		
		Note note = new Note(title: "John's note", text: 'some text')
		john.addToNotes(note)
		john.save(flush: true, failOnError: true)
		
		id = note.id
		
		assert User.count() == 2
		assert Note.count() == 1

		loginAsAnonymous()
	}
	
	
	void testGetTotalNoteCountAnonymous() {
		long noteCount = noteService.getTotalNoteCount()
		assert noteCount == 1
	}
	
	
	void testGetTotalNoteCountLoggedIn() {
		loginAs('paul')
		long noteCount = noteService.getTotalNoteCount()
		assert noteCount == 1
	}

	
	void testCreateNoteAnonymous() {
		shouldFail(AccessDeniedException) {
			noteService.createNote(new Note(title: 'foo', text: 'bar' ))
		}
	}
	
	
	void testCreateNoteLoggedIn() {
		loginAs('paul')
		noteService.createNote(new Note(title: 'foo', text: 'bar' ))
		
		Note note = Note.findByTitle('foo')
		assert note.text 	== 'bar'
	}
	
	
	void testGetNoteAnonymous() {
		shouldFail(AccessDeniedException) {
			noteService.getNote(id)
		}
	}
	
	
	void testGetNoteLoggedIn() {
		loginAs('paul')
		shouldFail(AccessDeniedException) {
			noteService.getNote(id)
		}
	}
	
	
	void testGetNoteAuthor() {
		loginAs('john')
		Note note = noteService.getNote(id)
		assert note.title == "John's note"
	}
	
	
	void testUpdateAnonymous() {
		Note note = Note.get(id)
		note.title = 'new title'
		
		shouldFail(AccessDeniedException) {
			noteService.updateNote(note)
		}
	}
	
	
	void testUpdateLoggedIn() {
		loginAs('paul')
		Note note = Note.get(id)
		note.title = 'new title'
		
		shouldFail(AccessDeniedException) {
			noteService.updateNote(note)
		}
	}
	
	
	void testUpdateAuthor() {
		loginAs('john')
		Note note = Note.get(id)
		note.title = 'new title'
		
		noteService.updateNote(note)
		
		Note dbNote = Note.get(id)
		assert dbNote.title == 'new title'
	}

	
	void testRemoveNoteUsingBeanResolverAnonymous() {
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingBeanResolver(id)
		}
	}
	
	
	void testRemoveNoteUsingBeanResolverLoggedIn() {
		loginAs('paul')
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingBeanResolver(id)
		}
	}
	
	
	void testRemoveNoteUsingBeanResolverAuthor() {
		loginAs('john')
		noteService.removeNoteUsingBeanResolver(id)
		assert Note.count() == 0
	}

	
	void testRemoveNoteUsingPermissionEvaluatorAnonymous() {
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingPermissionEvaluator(id)
		}
	}
	
	
	void testRemoveNoteUsingPermissionEvaluatorLoggedIn() {
		loginAs('paul')
		shouldFail(AccessDeniedException) {
			noteService.removeNoteUsingPermissionEvaluator(id)
		}
	}
	
	
	void testRemoveNoteUsingPermissionEvaluatorAuthor() {
		loginAs('john')
		noteService.removeNoteUsingPermissionEvaluator(id)
		assert Note.count() == 0
	}
	
	
	private void loginAs(String username) {
		springSecurityService.reauthenticate(username)
	}
	
	private void loginAsAnonymous() {
		SCH.context.setAuthentication(new AnonymousAuthenticationToken('key', 'Anonymous', AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")))
	}
	
	private void logout() {
		loginAsAnonymous()	
	}
}
