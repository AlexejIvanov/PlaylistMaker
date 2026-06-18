package com.example.playlistmaker.playlist.presentation.create

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.playlist.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    // Подменяем родительскую ViewModel на нашу наследницу (которая умеет обновлять БД)
    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Обязательно вызываем логику родителя

        // Получаем переданный плейлист из аргументов навигации
        val playlist = arguments?.getSerializable("playlist") as? Playlist

        playlist?.let { currentPlaylist ->
            // Передаем ID во ViewModel, чтобы знать, какую запись обновлять
            viewModel.currentPlaylistId = currentPlaylist.id.toInt()

            // 1. Меняем текст заголовка и кнопки в соответствии с вашими ID
            binding.toolbar.title = "Редактировать"
            binding.createButton.text = "Сохранить"

            // 2. Заполняем поля ввода текущими данными
            binding.nameEditText.setText(currentPlaylist.name)
            binding.descriptionEditText.setText(currentPlaylist.description ?: "")

            // 3. Загружаем обложку, если она была установлена
            if (!currentPlaylist.coverFilePath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(currentPlaylist.coverFilePath)
                    .centerCrop() // Скругление углов уже делает ShapeableImageView из вашей XML
                    .into(binding.playlistCover)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Перехватываем системный жест/кнопку "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }
}