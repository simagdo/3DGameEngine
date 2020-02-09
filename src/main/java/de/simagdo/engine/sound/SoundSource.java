package de.simagdo.engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {

    private final int sourceId;

    public SoundSource(boolean loop, boolean relative) {
        this.sourceId = alGenSources();

        if (loop) alSourcei(this.sourceId, AL_LOOPING, AL_TRUE);
        if (relative) alSourcei(this.sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
    }

    public void setBuffer(int bufferId) {
        this.stop();
        alSourcei(this.sourceId, AL_BUFFER, bufferId);
    }

    public void setPosition(Vector3f position) {
        alSource3f(this.sourceId, AL_POSITION, position.x, position.y, position.z);
    }

    public void setSpeed(Vector3f speed) {
        alSource3f(this.sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setGain(float gain) {
        alSourcef(this.sourceId, AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
        alSourcef(this.sourceId, param, value);
    }

    public void play() {
        alSourcePlay(this.sourceId);
    }

    public boolean isPlaying() {
        return alGetSourcei(this.sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(this.sourceId);
    }

    public void stop() {
        alSourceStop(this.sourceId);
    }

    public void cleanUp() {
        this.stop();
        alDeleteSources(this.sourceId);
    }

}
