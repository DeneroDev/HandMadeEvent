package com.example.denero.handmadeevent.EventList

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.denero.handmadeevent.R

import kotlinx.android.synthetic.main.activity_event_list.*

class EventListActivity : AppCompatActivity(),
        AllEventListFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)
        displayFragment(getString(R.string.key_all_event_fragment))
    }

    private fun displayFragment(keyFragment: String, bundle: Bundle? = null) {
        val transaction = supportFragmentManager.beginTransaction()

        when (keyFragment) {
            getString(R.string.key_all_event_fragment) -> fragment = AllEventListFragment()
        }

        fragment!!.arguments = bundle
        transaction.replace(R.id.frame_layout_event_list, fragment)
        transaction.commit()
    }
}
