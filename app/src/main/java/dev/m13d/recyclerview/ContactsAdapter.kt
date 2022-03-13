package dev.m13d.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.m13d.recyclerview.databinding.ItemContactBinding
import dev.m13d.recyclerview.model.Contact

interface ContactActionListener {

    fun onContactDetails(contact: Contact)
}

class ContactsDiffCallback(
    private val oldList: List<Contact>,
    private val newList: List<Contact>
): DiffUtil.Callback() {
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
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>()
    , View.OnClickListener {

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

        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contacts[position]

        with(holder.binding) {
            holder.itemView.tag = contact

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
        actionListener.onContactDetails(contact)
    }
}
