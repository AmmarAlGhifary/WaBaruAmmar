package com.blogspot.yourfavoritekaisar.wabaruammar.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.fragments.chats.ChatsFragment
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.fragments.status.StatusListFragment
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.fragments.status.StatusUpdateFragment

class SectionPagerAdapter (fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val chatFragment = ChatsFragment()
    private val statusUpdateFragment = StatusUpdateFragment()
    private val statusListFragment = StatusListFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> statusUpdateFragment
            1 -> chatFragment
            2 -> statusListFragment
            else -> chatFragment
        }
    }

    override fun getCount(): Int {
        return 3
    }
}