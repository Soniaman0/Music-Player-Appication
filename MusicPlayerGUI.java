import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    //color Configurations
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;
    private JFileChooser jFileChooser;
    private JLabel songTitle, songartist;
    private JPanel playbackButton;
    private JSlider musicSlider;

    public MusicPlayerGUI(){


        super("Music player");

        //Set frame backgrount color
        getContentPane().setBackground(FRAME_COLOR);

        //Set size Height Width
        setSize(400 ,600);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        setLayout(null);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();


        jFileChooser.setCurrentDirectory(new File("src/Images"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","MP3"));

        addGuiComponents();
    }

    private void addGuiComponents() {
        addToolbar();

        //Add record image
        JLabel songImage = new JLabel(addImage("src/Images/Record.jpg"));
        songImage.setBounds(0,50,getWidth() - 10,255);
        add(songImage);

        //Song title
        songTitle = new JLabel("Song title");
        songTitle.setBounds(0,295,getWidth() - 10, 30);
        songTitle.setFont(new Font("MONOSPACED",Font.BOLD,24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //Song Artist
        songartist = new JLabel("Artist");
        songartist.setBounds(0,325,getWidth() - 10, 30);
        songartist.setFont(new Font("MONOSPACED",Font.PLAIN,24));
        songartist.setForeground(TEXT_COLOR);
        songartist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songartist);

        //Music slider
        musicSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
        musicSlider.setBounds(getWidth()/2 - 300/2 , 365, 300 ,40);
        musicSlider.setBackground(null);
        //Use mouseListener for control musicSlider
        musicSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //Holding the tick we want to the pause the song
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //When the user drops the tick
                JSlider source = (JSlider) e.getSource();

                //Where the user wants to playback
                int frame = source.getValue();

                //The current frame in the music player to this frame
                musicPlayer.setCurrentFrame(frame);

                //current time in milli as well
                musicPlayer.setCurrentTimeInMilli((int)(frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                //resume song
                musicPlayer.playcurrentsong();

                //Pouse button and toggle off play button
                enablePlayButtonDisablePauseButton();
            }

        });
        add(musicSlider);

        //Playback Buttons
        addPlaybackBtns();

    }
    //Set Menu toolbar.
    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0,0, getWidth(), 25);

        toolBar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        //Set song menu
        JMenu smenu = new JMenu("song");
        menuBar.add(smenu);

        //Set load song in the Song
        JMenuItem Loadsong = new JMenuItem("Load song");
        Loadsong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    Song song = new Song(selectedFile.getPath());

                    musicPlayer.addSong(song);

                    //Update title and Artist
                    uploadTitlreAndArtist(song);

                    //Playback slider
                    playBackSlider(song);

                    //Disable play button enable Pause button
                    disablePlayButtonEnablePauseButton();

                }
            }
        });
        smenu.add(Loadsong);

        //Set Playlist
        JMenu playlist = new JMenu("Playlist");
        menuBar.add(playlist);

        //Set item in the playlist
        JMenuItem CreatePlaylist = new JMenuItem("Create Playlist");
        CreatePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Load music playlist dialog
                new playListDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playlist.add(CreatePlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));

                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile !=null){
                    //Stop the music
                    musicPlayer.stopSong();

                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        
        playlist.add(loadPlaylist);

        add(toolBar);
    }

    private void addPlaybackBtns() {
        playbackButton = new JPanel();
        playbackButton.setBounds(0,435,getWidth() - 10 , 80);
        playbackButton.setBackground(null);

        //Previous Button
        JButton previousbutton = new JButton(addImage("src/Images/previous.png"));
        previousbutton.setBorderPainted(false);
        previousbutton.setBackground(null);
        previousbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Go to the previous song
                musicPlayer.prevsong();

            }
        });
        playbackButton.add(previousbutton);

        //Play button
        JButton playbutton = new JButton(addImage("src/Images/play.png"));
        playbutton.setBorderPainted(false);
        playbutton.setBackground(null);
        playbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disablePlayButtonEnablePauseButton();

                musicPlayer.playcurrentsong();
            }
        });
        playbackButton.add(playbutton);

        //Pause button
        JButton pausebutton = new JButton(addImage("src/Images/pause.png"));
        pausebutton.setBorderPainted(false);
        pausebutton.setBackground(null);
        pausebutton.setVisible(false);
        pausebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();

                musicPlayer.pauseSong();
            }
        });
        playbackButton.add(pausebutton);

        //Next button
        JButton nextbutton = new JButton(addImage("src/Images/next.png"));
        nextbutton.setBorderPainted(false);
        nextbutton.setBackground(null);
        nextbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Go to the next song
                musicPlayer.nextSong();
            }
        });
        playbackButton.add(nextbutton);

        add(playbackButton);

    }

    //This is using to update song slider
    public void playbackSliderValue(int frame){
        musicSlider.setValue(frame);
    }

    public void uploadTitlreAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songartist.setText(song.getSongArtist());
    }

    public void playBackSlider(Song song){
        //Max slider
        musicSlider.setMaximum(song.getMp3File().getFrameCount());

        //Song length
        Hashtable<Integer , JLabel> lableTable = new Hashtable<>();

        //Start 00:00
        JLabel startSong = new JLabel("00:00");
        startSong.setFont(new Font("Dialog",Font.BOLD,18));
        startSong.setForeground(TEXT_COLOR);

        //Song end
        JLabel endSong = new JLabel(song.getSongLength());
        endSong.setFont(new Font("Dialog",Font.BOLD,18));
        endSong.setForeground(TEXT_COLOR);

        lableTable.put(0,startSong);
        lableTable.put(song.getMp3File().getFrameCount(),endSong);

        musicSlider.setLabelTable(lableTable);
        musicSlider.setPaintLabels(true);
    }

    public void disablePlayButtonEnablePauseButton(){
        JButton playButton = (JButton) playbackButton.getComponent(1);
        JButton pauseButton = (JButton) playbackButton.getComponent(2);

        //Play button off
        playButton.setVisible(false);
        playButton.setEnabled(false);

        //Pause button on
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton(){
        JButton playButton = (JButton) playbackButton.getComponent(1);
        JButton pauseButton = (JButton) playbackButton.getComponent(2);

        //Play button on
        playButton.setVisible(true);
        playButton.setEnabled(true);

        //Pause button off
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon addImage(String imagePath){
        try {
            //Given image path
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
