package dev.m13d.recyclerview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dev.m13d.recyclerview.databinding.FragmentListBinding
import dev.m13d.recyclerview.model.Contact
import dev.m13d.recyclerview.model.ContactListener
import dev.m13d.recyclerview.model.ContactService

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var _adapter: ContactsAdapter

    private lateinit var cardViewClickListener: CardViewClickListener

    private val contactsService: ContactService
        get() = (activity?.applicationContext as App).contactService

    private val contactsListener: ContactListener = {
        _adapter.contacts = it
    }

    private var list = mutableListOf<Contact>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cardViewClickListener = context as CardViewClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        _adapter = ContactsAdapter(object : ContactActionListener {
            override fun onContactDetails(contact: Contact) {
                cardViewClickListener.onCardViewClicked(contact)
            }

            val layoutManager = LinearLayoutManager(activity)
            override fun onUserMove(contact: Contact, moveBy: Int, contactPosition: Int) {
                contactsService.moveContact(contact, moveBy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (contactPosition == firstVisibleItemPosition ||
                    (contactPosition == firstVisibleItemPosition + 1 && moveBy < 0)
                ) {
                    val v = binding.recyclerView.getChildAt(0)
                    val offset = if (v == null) 0 else v.top - binding.recyclerView.paddingTop
                    layoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, offset)
                }
            }

            override fun onUserDelete(contact: Contact) {
                contactsService.deleteUser(contact)
            }
        })
        _adapter.contacts = contactsService.getContacts()

        navigator().listenResult(Contact::class.java, viewLifecycleOwner) {
            saveResult(it)
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = _adapter
        }

        val itemAnimator = binding.recyclerView.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }

        contactsService.addListener(contactsListener)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        contactsService.removeListener(contactsListener)
    }

    private fun saveResult(contact: Contact) {
        _adapter.contacts = contactsService.getContacts().map {
            if (it.id == contact.id)
                it.copy(name = contact.name)
            else
                it
        }
    }

    interface CardViewClickListener {
        fun onCardViewClicked(contact: Contact)
    }

    companion object {

        const val LIST_FRAGMENT_TAG = "LIST_FRAGMENT_TAG"

        @JvmStatic
        fun newInstance() = ListFragment()

    }
}
