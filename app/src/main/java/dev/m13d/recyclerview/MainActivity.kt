package dev.m13d.recyclerview

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import dev.m13d.recyclerview.DetailsFragment.Companion.DETAILS_FRAGMENT_TAG
import dev.m13d.recyclerview.ListFragment.Companion.LIST_FRAGMENT_TAG
import dev.m13d.recyclerview.databinding.ActivityMainBinding
import dev.m13d.recyclerview.model.Contact
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), ListFragment.CardViewClickListener, Navigator {

    private lateinit var binding: ActivityMainBinding
    private var isTablet by Delegates.notNull<Boolean>()

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isTablet = resources.getBoolean(R.bool.tablet)

        if (supportFragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG) == null) {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.fragment_container, ListFragment.newInstance(), LIST_FRAGMENT_TAG)
                commit()
            }
        }
    }

    override fun <T> showDetails(contact: Contact) {

        supportFragmentManager.beginTransaction().run {
            if (isTablet) {
                replace(R.id.fragment_details_container, DetailsFragment.newInstance(contact))
            } else {
                replace(R.id.fragment_container, DetailsFragment.newInstance(contact))
            }
            addToBackStack(DETAILS_FRAGMENT_TAG)
            commit()
        }
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun <T : Parcelable> publishResult(result: T) {
        supportFragmentManager.setFragmentResult(
            result.javaClass.name,
            bundleOf(KEY_RESULT to result)
        )
    }

    override fun <T : Parcelable> listenResult(
        clazz: Class<T>,
        owner: LifecycleOwner,
        listener: (T) -> Unit
    ) {
        supportFragmentManager.setFragmentResultListener(
            clazz.name,
            owner,
            FragmentResultListener { _, bundle ->
                listener.invoke(bundle.getParcelable(KEY_RESULT)!!)
            })
    }

    override fun onCardViewClicked(contact: Contact) {
        supportFragmentManager.beginTransaction().run {
            if (isTablet) {
                replace(R.id.fragment_details_container, DetailsFragment.newInstance(contact))
            } else {
                replace(R.id.fragment_container, DetailsFragment.newInstance(contact))
            }
            addToBackStack(DETAILS_FRAGMENT_TAG)
            commit()
        }
    }

    companion object {
        @JvmStatic
        private val KEY_RESULT = "RESULT"

    }
}
