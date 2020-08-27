package com.blogspot.yourfavoritekaisar.wabaruammar.ui.fragments.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.yourfavoritekaisar.wabaruammar.R
import com.blogspot.yourfavoritekaisar.wabaruammar.adapter.StatusListAdapter
import com.blogspot.yourfavoritekaisar.wabaruammar.listener.StatusItemClickListener
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.status.StatusActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.util.DATA_USERS
import com.blogspot.yourfavoritekaisar.wabaruammar.util.DATA_USER_CHATS
import com.blogspot.yourfavoritekaisar.wabaruammar.util.StatusListElement
import com.blogspot.yourfavoritekaisar.wabaruammar.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_status_list.*

class StatusListFragment : Fragment(), StatusItemClickListener {

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val statusListAdapter = StatusListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status_list, container, false)
    }

    override fun onItemClicked(statusElement: StatusListElement) {
        startActivity(StatusActivity.getIntent(context, statusElement))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusListAdapter.setOnItemClickListener(this)
        rv_status_list.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = statusListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        onVisible()

        fab_status_list.setOnClickListener {
            onVisible()
        }

    }

   private fun onVisible() {
        statusListAdapter.onRefresh()
        refreshList()
    }

    @Suppress("NAME_SHADOWING")
   private fun refreshList() {
        firebaseDb.collection(DATA_USERS).document(userId!!)
            .get()
            .addOnSuccessListener {
                if (it.contains(DATA_USER_CHATS)) {
                    val partners =
                        it[DATA_USER_CHATS]
                    for (partner in (partners as HashMap<*, *>).keys)
                        firebaseDb.collection(DATA_USERS).document(partner.toString())
                            .get()
                            .addOnSuccessListener {
                                val partner = it.toObject(User::class.java)
                                if (partner != null) {
                                    if (!partner.status.isNullOrEmpty() || !partner.statusUrl.isNullOrEmpty()) {
                                        val newElement = StatusListElement(
                                            partner.name,
                                            partner.imageUrl,
                                            partner.status,
                                            partner.statusUrl,
                                            partner.statusTime
                                        )
                                        statusListAdapter.addElement(newElement)
                                    }

                                }
                            }
                }
            }

    }
}