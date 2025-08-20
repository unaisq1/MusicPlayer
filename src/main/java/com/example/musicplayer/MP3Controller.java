package com.example.musicplayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class MP3Controller implements Initializable {
    
    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
	@FXML
	private Label volumeLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, prevButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar progressBar;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNum;
    private int[] speeds = {25,50,75,100,125,150,175,200};

    private Timer timer;
    private TimerTask task;
    private boolean running;
    

    @Override
    public void initialize(URL arg0, ResourceBundle resources) 
    {
        songs = new ArrayList<File>();

        directory = new File("music");

        files = directory.listFiles();

        if (files != null)
        {
            for(File file : files)
            {
                songs.add(file);
            }
        }
		
		media = new Media(songs.get(songNum).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songLabel.setText(songs.get(songNum).getName());
    
		for (int i = 0; i < speeds.length; i++)
		{
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}

		speedBox.setOnAction(this::changeSpeed);

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
				volumeLabel.setText(Double.toString(volumeSlider.getValue()));			
			}
		});

		progressBar.setStyle("-fx-accent: black");
	}


    public void playMedia() 
    {
		beginTimer();
        changeSpeed(null);
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);			
		mediaPlayer.play();
    }

    public void pauseMedia() 
    {
		cancelTimer();
		mediaPlayer.pause();
    }

    public void resetMedia() 
    {
		progressBar.setProgress((0));
		mediaPlayer.seek(Duration.seconds(0));
    }

    public void prevMedia() 
    {
		if(songNum > 0)
		{
			songNum--;

			mediaPlayer.stop();

			if (running == true)
			{
				cancelTimer();
			}

			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNum).getName());

			playMedia();
		}
		else
		{
			songNum = songs.size() - 1;

			mediaPlayer.stop();

			if (running == true)
			{
				cancelTimer();
			}

			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNum).getName());

			playMedia();
		}
    }


    public void nextMedia() 
    {
		if(songNum < songs.size() - 1)
		{
			songNum++;

			mediaPlayer.stop();

			if (running == true)
			{
				cancelTimer();
			}

			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNum).getName());

			playMedia();
		}
		else
		{
			songNum = 0;

			mediaPlayer.stop();

			if (running == true)
			{
				cancelTimer();
			}

			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			songLabel.setText(songs.get(songNum).getName());

			playMedia();
		}
    }

    public void changeSpeed(ActionEvent event) 
    {
		if (speedBox.getValue() == null)
		{
			mediaPlayer.setRate(1);
		}
		else
		{
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01); 
		}   
	}

    public void beginTimer() 
    {
		timer = new Timer();

		task = new TimerTask() 
		{
			
			public void run() 
			{
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				progressBar.setProgress((current/end));

				if (current/end == 1)
				{
					cancelTimer();
				}
			}

		};

		timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() 
    {
		running = false;
		timer.cancel();
    }
}