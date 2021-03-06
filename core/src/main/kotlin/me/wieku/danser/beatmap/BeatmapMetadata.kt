package me.wieku.danser.beatmap

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "BeatmapMetadata")
class BeatmapMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected var id: Int? = null

    var title = "Unknown title"
    var titleUnicode: String? = null
    var artist = "Unknown artist"
    var artistUnicode: String? = null
    var creator = "Unknown creator"
    var source: String? = null
    var tags: String? = null
    var audioFile: String = ".mp3"
    var previewTime = 0
    var backgroundFile: String? = null
    var videoFile: String? = null

    override fun equals(other: Any?): Boolean {
        if (other !is BeatmapMetadata) {
            return false
        }

        return other.title == this.title && Objects.equals(
            other.titleUnicode,
            this.titleUnicode
        ) && other.artist == this.artist && Objects.equals(
            other.artistUnicode,
            this.artistUnicode
        ) && other.creator == this.creator && Objects.equals(other.source, this.source) && Objects.equals(
            other.tags,
            this.tags
        ) && other.audioFile == this.audioFile && other.previewTime == this.previewTime && Objects.equals(
            other.backgroundFile,
            this.backgroundFile
        ) && Objects.equals(
            other.videoFile,
            this.videoFile
        )
    }

    override fun hashCode() = Objects.hash(creator, artist, title, audioFile)
}