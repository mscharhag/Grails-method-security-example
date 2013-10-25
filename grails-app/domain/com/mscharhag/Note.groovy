package com.mscharhag

class Note {
	String title
	String text
	static belongsTo = [author: User]
}
