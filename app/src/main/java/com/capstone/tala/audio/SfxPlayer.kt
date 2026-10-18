package com.capstone.tala.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.capstone.tala.R

enum class TalaSoundEffect {
    TAP,
    PICKUP,
    DROP,
    CORRECT,
    TRY_AGAIN,
    STAR,
    CELEBRATE
}

class SfxPlayer(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSounds = mutableSetOf<Int>()
    private val activeStreams = mutableSetOf<Int>()
    private var released = false
    private val soundIds: Map<TalaSoundEffect, Int>

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (!released && status == 0) loadedSounds += sampleId
        }
        soundIds = mapOf(
            TalaSoundEffect.TAP to loadSound(context, R.raw.sfx_tap),
            TalaSoundEffect.PICKUP to loadSound(context, R.raw.sfx_pickup),
            TalaSoundEffect.DROP to loadSound(context, R.raw.sfx_drop),
            TalaSoundEffect.CORRECT to loadSound(context, R.raw.sfx_success),
            TalaSoundEffect.TRY_AGAIN to loadSound(context, R.raw.sfx_try_again),
            TalaSoundEffect.STAR to loadSound(context, R.raw.sfx_star),
            TalaSoundEffect.CELEBRATE to loadSound(context, R.raw.sfx_celebrate)
        )
    }

    private fun loadSound(context: Context, @RawRes rawId: Int): Int =
        soundPool.load(context, rawId, 1)

    fun play(effect: TalaSoundEffect): Boolean {
        if (released) return false
        val soundId = soundIds[effect] ?: return false
        if (soundId !in loadedSounds) return false
        val streamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        if (streamId == 0) return false
        if (activeStreams.size >= 4) {
            val oldestStream = activeStreams.first()
            soundPool.stop(oldestStream)
            activeStreams.remove(oldestStream)
        }
        activeStreams += streamId
        return true
    }

    fun playTap() = play(TalaSoundEffect.TAP)
    fun playSuccess() = play(TalaSoundEffect.CORRECT)
    fun playCelebrate() = play(TalaSoundEffect.CELEBRATE)

    fun stopAll() {
        activeStreams.forEach(soundPool::stop)
        activeStreams.clear()
    }

    fun release() {
        if (released) return
        released = true
        stopAll()
        soundPool.release()
    }
}
