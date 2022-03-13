package dev.m13d.recyclerview.model

import com.github.javafaker.Faker

typealias ContactListener = (users: List<Contact>) -> Unit

class ContactService {

    private var contacts = mutableListOf<Contact>()
//    private val listeners = mutableSetOf<ContactListener>()

    init {
        val faker = Faker.instance()
        contacts = (0..113).map { Contact(
            id = it,
            name = faker.name().name(),
            surname = faker.name().lastName(),
            phone = faker.phoneNumber().cellPhone(),
            photo = "https://picsum.photos/id/1$it/300/300"
        ) }.toMutableList()
    }


/*
    fun addListener(listener: ContactListener) {
        listeners.add(listener)
        listener.invoke(contacts)
    }

    fun removeListener(listener: ContactListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(contacts) }
    }


    private fun findIndexById(contactId: Int): Int = contacts.indexOfFirst { it.id == contactId }
*/
    fun getContacts() = contacts
}
