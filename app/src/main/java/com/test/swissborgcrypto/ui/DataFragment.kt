package com.test.swissborgcrypto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.swissborgcrypto.R
import com.test.swissborgcrypto.databinding.DataFragmentLayoutBinding
import com.test.swissborgcrypto.di.components.DaggerCryptoDataComponent
import com.test.swissborgcrypto.repositories.WebsocketRepository
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel
import com.test.swissborgcrypto.viewmodels.ViewModelFactory
import javax.inject.Inject

class DataFragment : Fragment() {
    lateinit var fragmentBinding: DataFragmentLayoutBinding
    lateinit var dataFragmentViewModel: DataFragmentViewModel

    @Inject
    lateinit var websocketRepository: WebsocketRepository

    init {
        DaggerCryptoDataComponent.create().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.data_fragment_layout, container, false)
        fragmentBinding.dataViewModel = dataFragmentViewModel
        fragmentBinding.lifecycleOwner = this@DataFragment
        return fragmentBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            dataFragmentViewModel =
                ViewModelProvider(it, ViewModelFactory(websocketRepository)).get(DataFragmentViewModel::class.java)
        }
    }

    companion object {
        fun newInstance(): DataFragment {
            return DataFragment()
        }
    }
}