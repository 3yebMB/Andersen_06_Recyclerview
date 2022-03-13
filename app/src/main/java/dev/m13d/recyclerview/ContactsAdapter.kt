package dev.m13d.recyclerview

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.m13d.recyclerview.databinding.ItemContactBinding
import dev.m13d.recyclerview.model.Contact

interface ContactActionListener {

    fun onContactDetails(contact: Contact)
    fun onUserMove(contact: Contact, moveBy: Int, contactPosition: Int)
    fun onUserDelete(contact: Contact)

}

class ContactsDiffCallback(
    private val oldList: List<Contact>,
    private val newList: List<Contact>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldContact = oldList[oldItemPosition]
        val newContact = newList[newItemPosition]
        return oldContact.id == newContact.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldContact = oldList[oldItemPosition]
        val newContact = newList[newItemPosition]
        return oldContact == newContact
    }
}

class ContactsAdapter(
    private val actionListener: ContactActionListener
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>(), View.OnClickListener {

    var contacts = emptyList<Contact>()
        set(newValue) {
            val diffCallback = ContactsDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    inner class ContactsViewHolder(
        val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.moreButton.setOnClickListener(this)

        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contacts[position]

        with(holder.binding) {
            holder.itemView.tag = contact
            moreButton.tag = contact

            tvName.text = contact.name
            tvSurname.text = contact.surname
            tvPhone.text = contact.phone

            if (contact.photo.isNotBlank()) {
                Glide.with(preview.context)
                    .load(contact.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_baseline_person_pin_24)
                    .error(R.drawable.ic_baseline_person_pin_24)
                    .into(preview)
            } else {
                Glide.with(preview.context).clear(preview)
                preview.setImageResource(R.drawable.ic_baseline_person_pin_24)
            }
        }
    }

    override fun getItemCount(): Int = contacts.size

    override fun onClick(v: View) {
        val contact = v.tag as Contact
        when (v.id) {
            R.id.moreButton -> {
                showPopupMenu(v)
            }
            else -> {
                actionListener.onContactDetails(contact)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as Contact
        val position = contacts.indexOfFirst { it.id == user.id }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < contacts.size - 1
        }
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> {
                    actionListener.onUserMove(user, -1, position)
                }
                ID_MOVE_DOWN -> {
                    actionListener.onUserMove(user, 1, position)
                }
                ID_REMOVE -> {
                    actionListener.onUserDelete(user)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
    }
}
