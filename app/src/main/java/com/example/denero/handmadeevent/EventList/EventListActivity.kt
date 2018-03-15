package com.example.denero.handmadeevent.EventList


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.denero.handmadeevent.R

import java.io.Serializable

class EventListActivity : AppCompatActivity()
        , AllEventListFragment.OnAllEventFragmentListener
        , DisplayFullEventFragment.OnDisplayFullEventFragmentListener
        , SignedEventFragment.OnFragmentInteractionListener {


    override fun closeMe(keyFragment: String) {
        closeFragment(keyFragment)
    }


    override fun getEventSelectedId(idEventSelected: String) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.key_id_event_selected), idEventSelected)
        displayFragment(getString(R.string.key_full_event_fragment), bundle = bundle)
    }

    private var fragment: Fragment? = null
    private val LOG_TAG = "GOT"
    private val LOG_HEAD = EventListActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        //TODO: добавить человеческое условие
        if (intent.hasExtra(getString(R.string.key_id_event_selected))) {
            val bundle = Bundle()
            bundle.putString(getString(R.string.key_id_event_selected), intent.getStringExtra(getString(R.string.key_id_event_selected)))
            displayFragment(getString(R.string.key_full_event_fragment), bundle = bundle)
        } else {
            if (intent.hasExtra(getString(R.string.key_signed_events_fragment))){
                displayFragment(getString(R.string.key_signed_events_fragment))
            } else {
                displayFragment(getString(R.string.key_all_event_fragment))
            }


        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_event_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.show_all_event -> {
                displayFragment(getString(R.string.key_all_event_fragment))
                return true
            }
            R.id.show_signed_event -> {
                displayFragment(getString(R.string.key_signed_events_fragment))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayFragment(keyFragment: String, bundle: Bundle? = null) {
        val transaction = supportFragmentManager.beginTransaction()

        when (keyFragment) {
            getString(R.string.key_all_event_fragment) -> fragment = AllEventListFragment()
            getString(R.string.key_signed_events_fragment) -> fragment = SignedEventFragment()
            getString(R.string.key_full_event_fragment) -> {
                fragment = DisplayFullEventFragment()
                transaction.addToBackStack(getString(R.string.key_full_event_fragment))
            }
        }

        fragment!!.arguments = bundle
        transaction.replace(R.id.frame_layout_event_list, fragment)
        transaction.commit()
    }

    private fun closeFragment(keyFragment: String) {
        val transaction = supportFragmentManager.beginTransaction()
        when (keyFragment) {
            getString(R.string.key_all_event_fragment) -> fragment = AllEventListFragment()
            getString(R.string.key_full_event_fragment) -> fragment = DisplayFullEventFragment()

        }
        transaction.addToBackStack(null)
        transaction.remove(fragment)

    }

    private fun pushLog(topic: String, message: Serializable) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}
