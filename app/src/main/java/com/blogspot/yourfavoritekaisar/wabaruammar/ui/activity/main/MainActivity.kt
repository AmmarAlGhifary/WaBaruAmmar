package com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blogspot.yourfavoritekaisar.wabaruammar.R
import com.blogspot.yourfavoritekaisar.wabaruammar.adapter.SectionPagerAdapter
import com.blogspot.yourfavoritekaisar.wabaruammar.listener.FailureCallback
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.contacts.ContactsActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.login.LoginActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.profile.ProfileActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.fragments.chats.ChatsFragment
import com.blogspot.yourfavoritekaisar.wabaruammar.util.DATA_USERS
import com.blogspot.yourfavoritekaisar.wabaruammar.util.DATA_USER_PHONE
import com.blogspot.yourfavoritekaisar.wabaruammar.util.PERMISSION_REQUEST_READ_CONTACT
import com.blogspot.yourfavoritekaisar.wabaruammar.util.REQUEST_NEW_CHATS
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FailureCallback {

    companion object {
        const val PARAM_NAME = "name"
        const val PARAM_PHONE = "phone"
    }

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mSectionPagerAdapter: SectionPagerAdapter
    private val firebaseDb = FirebaseFirestore.getInstance()
    private val chatsFragment = ChatsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatsFragment.setFailureCallBackListener(this)

        setSupportActionBar(toolbar)
        mSectionPagerAdapter =
            SectionPagerAdapter(
                supportFragmentManager
            )

        container.adapter = mSectionPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        resizeTabs()
        tabs.getTabAt(1)?.select()


        fab.setOnClickListener {
            onNewChat()
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> fab.hide()
                    1 -> fab.show()
                    3 -> fab.hide()
                }
            }

        })
    }

    private fun onNewChat() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED
        ) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Contacts permission")
                    .setMessage("This App Requires Access your Contacs to Initiation A Concersation")
                    .setPositiveButton("YES") { _, _ ->
                        requestContactPermission()
                    }
                    .setNegativeButton("NO") { _, _ ->

                    }
                    .show()

            } else {
                requestContactPermission()
            }
        } else {
            startNewActivity()
        }

    }

    private fun startNewActivity() {
        startActivityForResult(Intent(this, ContactsActivity::class.java), REQUEST_NEW_CHATS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_NEW_CHATS -> {
                    val name = data?.getStringExtra(PARAM_NAME) ?: ""
                    val phone = data?.getStringExtra(PARAM_PHONE) ?: ""
                    checkNewChatUser(name, phone)
                }
            }
        }
    }

    private fun checkNewChatUser(name: String, phone: String) {
        if (!name.isEmpty() && !phone.isEmpty()) {
            firebaseDb.collection(DATA_USERS)
                .whereEqualTo(DATA_USER_PHONE, phone
                )
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        chatsFragment.newChat(it.documents[0].id)
                    } else {
                        AlertDialog.Builder(this).setTitle("User Not Found")
                            .setMessage("$name does not have an account. Send them an SMS to install this app.")

                            .setPositiveButton("OK") { dialog, which ->
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("sms:$phone")
                                intent.putExtra(
                                    "sms_body",
                                    "halohalolhaohalohalohlq."
                                )
                                startActivity(intent)
                            }

                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "haha mampus",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
        }
    }

    private fun requestContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_READ_CONTACT
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewActivity()     // memulai activity yang lain dengan memakai intent
                }
            }
        }
    }

    private fun resizeTabs() {
        val layout = (tabs.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0.4f
        layout.layoutParams = layoutParams
    }

    override fun onResume() {
        super.onResume()
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> onLogout()
            R.id.action_profile -> onProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }

    private fun onProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun userError() {
        Toast.makeText(this, "User Not Found", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}