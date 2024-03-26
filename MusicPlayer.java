import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {

    //Used to update isPaused
    private static final Object playSingle = new Object();

    //Update the GUI in class
    private MusicPlayerGUI musicPlayerGUI;

    //Creating a song class
    private Song currentSong;

    public Song getCurrentSong(){
        return currentSong;
    }

    private ArrayList<Song> playList;

    private int currentPlayListIndex;

    //AdvancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    //The player has been paused
    private boolean isPaused;

    private boolean songFinished;

    private boolean pressNext , pressprevious;

    //Last frame when the playback is finished
    private int currentFrame;
    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }

    //Track Millisecond has passed
    private int currentTimeInMilli;
    public void setCurrentTimeInMilli(int timeInMilli){
        currentTimeInMilli = timeInMilli;
    }

    //constructor
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI){
        this.musicPlayerGUI = musicPlayerGUI;
    }
    public void addSong(Song song){
        currentSong = song;
        playList = null;

        if (!songFinished)
            stopSong();

        if (currentSong != null){
            currentFrame = 0;

            currentTimeInMilli = 0;

            musicPlayerGUI.playbackSliderValue(0);

            playcurrentsong();
        }
    }

    public void loadPlaylist(File playlistFile){
        playList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath;
            while ((songPath = bufferedReader.readLine())!=null){
                Song song = new Song(songPath);

                playList.add(song);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if (playList.size() > 0){
            musicPlayerGUI.playbackSliderValue(0);
            currentTimeInMilli = 0;

            currentSong = playList.get(0);

            currentFrame = 0;

            musicPlayerGUI.disablePlayButtonEnablePauseButton();
            musicPlayerGUI.uploadTitlreAndArtist(currentSong);
            musicPlayerGUI.playBackSlider(currentSong);

            playcurrentsong();
        }
    }

    public void pauseSong(){
        if (advancedPlayer != null){
             isPaused = true;

             stopSong();
        }
    }

    public void stopSong(){
        if (advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong(){
        if (playList == null) return;

        if (currentPlayListIndex + 1 > playList.size() - 1) return;

        pressNext = true;

        //Stop the song
        if (!songFinished)
            stopSong();

        currentPlayListIndex++;

        //Update current song
        currentSong = playList.get(currentPlayListIndex);

        currentFrame = 0;

        //Reset current time
        currentTimeInMilli = 0;

        //Update GUI
        musicPlayerGUI.disablePlayButtonEnablePauseButton();
        musicPlayerGUI.uploadTitlreAndArtist(currentSong);
        musicPlayerGUI.playBackSlider(currentSong);

        playcurrentsong();

    }

    public void prevsong(){

        if (playList == null) return;

        if (currentPlayListIndex - 1 < 0) return;

        pressprevious = true;

        //Stop the song
        if (!songFinished)
            stopSong();

        currentPlayListIndex--;

        //Update current song
        currentSong = playList.get(currentPlayListIndex);

        currentFrame = 0;

        //Reset current time
        currentTimeInMilli = 0;

        //Update GUI
        musicPlayerGUI.disablePlayButtonEnablePauseButton();
        musicPlayerGUI.uploadTitlreAndArtist(currentSong);
        musicPlayerGUI.playBackSlider(currentSong);

        playcurrentsong();
    }

    public void playcurrentsong(){
        if (currentSong == null) return;
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();

            startplaybackSliderThared();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isPaused){
                        synchronized (playSingle){
                            isPaused = false;

                            playSingle.notify();
                        }
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }else {
                        advancedPlayer.play();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void startplaybackSliderThared(){
    new Thread(new Runnable() {
        @Override
        public void run() {
            if (isPaused){
                try {

                    synchronized (playSingle){
                        playSingle.wait();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            while (!isPaused && !songFinished && !pressNext && !pressprevious){
                try{ //Current time milli
                    currentTimeInMilli++;

                    //Calculate frame value
                    int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08 * currentSong.getFrameRatePerMilliseconds());

                    //Update GUI
                    musicPlayerGUI.playbackSliderValue(calculatedFrame);

                    Thread.sleep(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Start");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");
        songFinished = false;
        pressNext = false;
        pressprevious = false;

//        System.out.println("Stopped @" + evt.getFrame());
        if (isPaused){
            currentFrame +=(int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }else {
            if (pressprevious || pressNext) return;
            //The song end
            songFinished = true;

            if (playList == null){
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            }else {
                if (currentPlayListIndex == playList.size() - 1){
                    musicPlayerGUI.enablePlayButtonDisablePauseButton();
                }else {
                    nextSong();
                }
            }
        }
    }
}
