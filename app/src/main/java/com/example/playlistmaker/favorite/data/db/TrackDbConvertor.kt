package com.example.playlistmaker.favorite.data.db
import com.example.playlistmaker.core.models.Track
import com.example.playlistmaker.favorite.data.db.entity.TrackEntity

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            trackTimeMillis = track.trackTimeMillis,
            addedTimestamp = System.currentTimeMillis()
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            artworkUrl100 = trackEntity.artworkUrl100,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            trackTimeMillis = trackEntity.trackTimeMillis,
            isFavorite = true
        )
    }
}
