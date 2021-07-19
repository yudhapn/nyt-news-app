package id.ypran.kozziinews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ypran.core.util.contentView
import id.ypran.kozziinews.R
import id.ypran.kozziinews.databinding.ActivityHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {
    private val binding: ActivityHomeBinding by contentView(R.layout.activity_home)
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.navHostFragment
    }
}