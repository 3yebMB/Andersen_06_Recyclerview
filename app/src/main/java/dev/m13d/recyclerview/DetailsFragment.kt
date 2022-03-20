package dev.m13d.recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dev.m13d.recyclerview.databinding.FragmentDetailsBinding
import dev.m13d.recyclerview.model.Contact
import java.lang.Exception

private const val ARG_PARAM1 = "param1"

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contact = it.getParcelable(ARG_PARAM1) ?: throw Exception("Not found contact")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        with(binding) {
            etName.setText(contact.name)
            etSurname.setText(contact.surname)
            etPhone.setText(contact.phone)
            if (contact.photo.isNotBlank()) {
                Glide.with(iview.context)
                    .load(contact.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_baseline_person_pin_24)
                    .error(R.drawable.ic_baseline_person_pin_24)
                    .into(iview)
            } else {
                Glide.with(iview.context).clear(iview)
                iview.setImageResource(R.drawable.ic_baseline_person_pin_24)
            }

            btnCancel.setOnClickListener { onCancelPressed() }
            btnSave.setOnClickListener { onConfirmPressed() }
        }

        return binding.root
    }

    private fun onConfirmPressed() {
        val result = contact.copy(
            name = if (binding.etName.text.isNotBlank()) binding.etName.text.toString()
            else {
                showError()
                return
            },
            surname = if (binding.etSurname.text.isNotBlank()) binding.etSurname.text.toString()
            else {
                showError()
                return
            },
            phone = if (binding.etPhone.text.isNotBlank()) binding.etPhone.text.toString()
            else {
                showError()
                return
            }
        )
        navigator().publishResult(result = result)
        navigator().goBack()
    }

    private fun showError() {
        Toast.makeText(requireActivity(), "This field must not be empty", Toast.LENGTH_SHORT).show()
    }

    private fun onCancelPressed() {
        navigator().goBack()
    }

    companion object {

        const val DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT_TAG"

        @JvmStatic
        fun newInstance(contact: Contact) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, contact)
                }
            }
    }
}
