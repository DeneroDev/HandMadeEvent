package com.example.denero.handmadeevent.EventList


import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.denero.handmadeevent.CreatedNewEventActivity
import com.example.denero.handmadeevent.R
import kotlinx.android.synthetic.main.activity_created_new_event.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*

import java.io.Serializable
//TODO:переименуй интерфейсы
class EventListActivity : AppCompatActivity()
        , AllEventListFragment.OnAllEventFragmentListener
        , DisplayFullEventFragment.OnDisplayFullEventFragmentListener
        , SignedEventFragment.OnSignedEventFragmentListener
        , MyEventListFragment.OnMyEventListFragmentListener {




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

        pushLog("AAAAAAAAAAAAAAAAAAAAAAAAAAAA","CCCCCCCCCCCCCCCCCCCC")
        if (intent.hasExtra(getString(R.string.key_mission_open_fragment))) {
            pushLog("key_mission_open_fragment", intent.getStringExtra(getString(R.string.key_mission_open_fragment)))
            pushLog("key_id_event_selected", intent.getStringExtra(getString(R.string.key_id_event_selected)))
            pushLog("CCCCCCCCCCCCCCCCCCCCCCCCCCCC","")
        }

        if (intent.hasExtra(getString(R.string.key_mission_open_fragment))) {
            when (intent.getStringExtra(getString(R.string.key_mission_open_fragment))) {
                getString(R.string.key_signed_event_fragment) -> {
                    displayFragment(getString(R.string.key_signed_event_fragment))
                }
                getString(R.string.key_full_event_fragment) -> {
                    if (intent.hasExtra(getString(R.string.key_id_event_selected))) {
                        val bundle = Bundle()
                        bundle.putString(getString(R.string.key_id_event_selected), intent.getStringExtra(getString(R.string.key_id_event_selected)))
                        displayFragment(getString(R.string.key_full_event_fragment), bundle = bundle)
                    } else {
                        displayFragment(getString(R.string.key_all_event_fragment))
                    }
                }
                getString(R.string.key_my_event_fragment) -> {
                    displayFragment(getString(R.string.key_my_event_fragment))
                }
                else -> {
                    displayFragment(getString(R.string.key_all_event_fragment))
                }
            }
        } else {
            displayFragment(getString(R.string.key_all_event_fragment))
        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.action_add_event -> {startActivity(Intent(applicationContext,CreatedNewEventActivity::class.java))}
                R.id.action_all_event -> { displayFragment(getString(R.string.key_all_event_fragment))}
                R.id.action_open_favorites -> { displayFragment(getString(R.string.key_signed_event_fragment))}
            }
            false
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
                displayFragment(getString(R.string.key_signed_event_fragment))
                return true
            }
            R.id.show_my_event -> {
                displayFragment(getString(R.string.key_my_event_fragment))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayFragment(keyFragment: String, bundle: Bundle? = null) {
        val transaction = supportFragmentManager.beginTransaction()

        when (keyFragment) {
            getString(R.string.key_all_event_fragment) -> fragment = AllEventListFragment()
            getString(R.string.key_signed_event_fragment) -> fragment = SignedEventFragment()
            getString(R.string.key_full_event_fragment) -> {
                fragment = DisplayFullEventFragment()
                transaction.addToBackStack(getString(R.string.key_full_event_fragment))
            }
            getString(R.string.key_my_event_fragment) -> fragment = MyEventListFragment()
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
