import javax.sound.sampled.*;
import java.io.File;

public class SoundClip
{
    Clip[] clips;
    int pos;

    SoundClip(String filename, int copies) throws Exception
    {
        clips = new Clip[copies];
        for(int i = 0; i < copies; i++)
        {
            AudioInputStream inputStream =
                    AudioSystem.getAudioInputStream(new File(filename));

            AudioFormat format = inputStream.getFormat();
            DataLine.Info info =
                    new DataLine.Info(Clip.class, format);

            clips[i] = (Clip)AudioSystem.getLine(info);
            clips[i].open(inputStream);
        }
        pos = 0;
    }

    void play()
    {
        clips[pos].setFramePosition(0);
        clips[pos].loop(0);

        if(++pos >= clips.length)
            pos = 0;
    }
}
