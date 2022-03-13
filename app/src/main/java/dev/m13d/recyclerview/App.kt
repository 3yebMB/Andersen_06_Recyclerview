package dev.m13d.recyclerview

import android.app.Application
import dev.m13d.recyclerview.model.ContactService

class App: Application() {

    val contactService = ContactService()
}
