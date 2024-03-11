/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gameEngine;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 *
 * @author txaber gardeazabal
 */
public class Sound {
    private int bufferId;
    private int sourceId;
    private String filepath;
    
    private boolean isPlaying = false;
    
    public Sound(String filepath, boolean loops) {
        this.filepath = filepath;
        
        // Allocate space to store the return information from stb
        stackPush();
        IntBuffer channelBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);
        
        ShortBuffer rawAudioBuffer = 
                stb_vorbis_decode_filename(filepath, channelBuffer,sampleRateBuffer);
        if (rawAudioBuffer == null) {
            System.out.println("Could not load sound: "+filepath);
            stackPop();
            stackPop();
            return;
        }
        
        // retrieve the extra information that was stored in the buffers by stb
        int channels = channelBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stackPop();
        stackPop();
        
        // find the correct openAl format
        int format = -1;
        switch(channels) {
            case 1: 
                format = AL_FORMAT_MONO16; break;
            case 2:
                format = AL_FORMAT_STEREO16; break;
        }
        
        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);
        
        // generate the source
        sourceId = alGenSources();
        
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, loops ? 1 : 0);
        alSourcei(sourceId, AL_POSITION, 0);
        alSourcef(sourceId, AL_GAIN, 0.3f);
        
        // free the stb raw audio buffer
        free(rawAudioBuffer);
    }
    
    public void delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }
    
    public void play() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
            alSourcei(sourceId, AL_POSITION, 0);
        }
        
        if (!isPlaying) {
            isPlaying = true;
        } 
        alSourcePlay(sourceId);
    }
    
    public void playIfNotPlaying() {
        if (!isPlaying()) {
            play();
        }
    }
    
    public void stop() {
        if (isPlaying) {
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public String getFilepath() {
        return filepath;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }
}
