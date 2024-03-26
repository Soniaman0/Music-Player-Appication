import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class playListDialog extends JDialog {
    private MusicPlayerGUI musicPlayerGUI;

    private ArrayList<String> songPath;
    public playListDialog(MusicPlayerGUI musicPlayerGUI){
        this.musicPlayerGUI = musicPlayerGUI;
        songPath = new ArrayList<>();

        //Dialog configure
        setTitle("Create Playlist");
        setSize(400,400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }
    private void addDialogComponents(){
        JPanel songContaier = new JPanel();
        songContaier.setLayout(new BoxLayout(songContaier,BoxLayout.Y_AXIS));
        songContaier.setBounds((int) (getWidth() * 0.025),10,(int)(getWidth() * 0.90),(int)(getWidth() * 0.75));
        add(songContaier);

        //Song Button add
        JButton songButtonAdd = new JButton("Add");
        songButtonAdd.setBounds(60 ,(int)(getHeight() * 0.80), 100,25);
        songButtonAdd.setFont(new Font("Dialog" , Font.BOLD,14));
        songButtonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3" , "mp3"));
                jFileChooser.setCurrentDirectory(new File("src/Images"));
                int result = jFileChooser.showOpenDialog(playListDialog.this);

                File selectedFile= jFileChooser.getSelectedFile();
                if (result == JFileChooser.APPROVE_OPTION && selectedFile !=null){
                    JLabel filePathLable = new JLabel(selectedFile.getPath());
                    filePathLable.setFont(new Font("Dialog",Font.BOLD,12));
                    filePathLable.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    songPath.add(filePathLable.getText());
                    songContaier.add(filePathLable);
                    songContaier.revalidate();
                }
            }
        });
        add(songButtonAdd);

        //Playlist Save Button
        JButton playListSaveButton = new JButton("Save");
        playListSaveButton.setBounds(215 ,(int)(getHeight() * 0.80), 100,25);
        playListSaveButton.setFont(new Font("Dialog" , Font.BOLD,14));
        playListSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              try {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("src/Images"));
                    int result = jFileChooser.showOpenDialog(playListDialog.this);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = jFileChooser.getSelectedFile();

                        //Convert to .txt file if not done already
                        //Check to see if the file does not hove the ".txt" file extension
                        if (!selectedFile.getName().substring(selectedFile.getName().length() - 4).equalsIgnoreCase(".txt")){
                            selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                        }

                        //The new file at the destinated directory
                        selectedFile.createNewFile();

                        //Write all of the paths into this file
                        FileWriter fileWriter = new FileWriter(selectedFile);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        for (String songPath : songPath){
                            bufferedWriter.write(songPath + "\n");
                        }
                        bufferedWriter.close();

                        JOptionPane.showMessageDialog(playListDialog.this , "Successfully Created PlayList");

                        playListDialog.this.dispose();
                    }
                }catch (Exception exception){
                  exception.printStackTrace();
              }
            }
        });
        add(playListSaveButton);
    }
}
