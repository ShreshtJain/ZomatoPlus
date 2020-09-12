package com.example.zomato_plus.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zomato_plus.R
import com.example.zomato_plus.util.drawerlocker

/**
 * A simple [Fragment] subclass.
 */
class FAQFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faqs, container, false)
        (activity as drawerlocker).setDrawerEnabled(true)
        return view
    }


}
