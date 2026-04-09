package com.example.playlistmaker.media.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupWindowInsets(view) // Добавление логики отступов

        // ВАЖНО: Используем childFragmentManager для вложенных фрагментов
        viewPager.adapter = MediaViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        // Настройка TabLayoutMediator
        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    private fun initViews(view: View) {
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
    }

    /**
     * Настройка отступов для Edge-to-Edge режима.
     */
    private fun setupWindowInsets(view: View) {
        val rootMedia = view.findViewById<View>(R.id.media) // ID из вашего XML fragment_media

        ViewCompat.setOnApplyWindowInsetsListener(rootMedia) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,    // Отступ под статус-бар
                left = systemBars.left,  // Боковые отступы для вырезов
                right = systemBars.right,
                bottom = systemBars.bottom // Отступ снизу (учитывает BottomNav)
            )
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Обязательно отсоединяем медиатор во избежание утечек памяти
        if (::tabMediator.isInitialized) {
            tabMediator.detach()
        }
    }
}