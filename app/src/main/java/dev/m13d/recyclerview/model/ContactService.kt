package dev.m13d.recyclerview.model

import com.github.javafaker.Faker
import java.util.*
import kotlin.collections.ArrayList

typealias ContactListener = (users: List<Contact>) -> Unit

class ContactService {

    private var contacts = mutableListOf<Contact>()
    private val listeners = mutableSetOf<ContactListener>()

    init {
        val faker = Faker.instance()
        contacts = (0..113).map { Contact(
            id = it,
            name = faker.name().firstName(),
            surname = faker.name().lastName(),
            phone = faker.phoneNumber().cellPhone(),
            photo = "https://picsum.photos/id/1$it/300/300"
        ) }.toMutableList()
    }

    fun deleteUser(contact: Contact) {
        val indexToDelete = findIndexById(contact.id)
        if (indexToDelete != -1) {
            contacts = ArrayList(contacts)
            contacts.removeAt(indexToDelete)
            notifyChanges()
        }
    }

    fun moveContact(contact: Contact, moveBy: Int) {
        val oldIndex = findIndexById(contact.id)
        if (oldIndex == -1) return
        val newIndex = oldIndex + moveBy
        if (newIndex < 0 || newIndex >= contacts.size) return
        contacts = ArrayList(contacts)
        Collections.swap(contacts, oldIndex, newIndex)
        notifyChanges()
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(contacts) }
    }

    private fun findIndexById(contactId: Int): Int = contacts.indexOfFirst { it.id == contactId }


    fun getContacts() = contacts

    fun addListener(listener: ContactListener) {
        listeners.add(listener)
        listener.invoke(contacts)
    }

    fun removeListener(listener: ContactListener) {
        listeners.remove(listener)
    }
}
