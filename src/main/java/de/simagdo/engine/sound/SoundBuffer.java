package de.simagdo.engine.sound;

import de.simagdo.utils.Utils;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundBuffer {

    private final int bufferId;
    private ShortBuffer pcm = null;
    private ByteBuffer vorbis = null;

    public SoundBuffer(String file) throws Exception {
        this.bufferId = alGenBuffers();
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(file, 32 * 1024, info);

            // Copy to buffer
            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
        }
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public void cleanup() {
        alDeleteBuffers(this.bufferId);
        if (this.pcm != null) {
            MemoryUtil.memFree(this.pcm);
        }
    }

    private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.vorbis = Utils.ioResourceToByteBuffer(resource, bufferSize);
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(this.vorbis, error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            this.pcm = MemoryUtil.memAllocShort(lengthSamples);

            this.pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, this.pcm) * channels);
            stb_vorbis_close(decoder);

            return this.pcm;
        }
    }

}
