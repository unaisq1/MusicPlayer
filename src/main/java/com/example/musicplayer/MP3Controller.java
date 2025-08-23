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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
	private Label speedLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, prevButton, nextButton;
	@FXML
	private TextField speedField;
    @FXML
    private Slider volumeSlider;
	@FXML
	private Slider speedSlider;
    @FXML
    private ProgressBar progressBar;
	@FXML
	private Label messageBox;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNum;
	private double currentSpeed = 1;

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

		String imagePath = getClass().getResource("/playSmall.png").toExternalForm();
		//System.out.println(imagePath);
		playButton.setStyle("-fx-background-image: url('" + imagePath + "'); " + "-fx-background-color: transparent; ");		
		
		media = new Media(songs.get(songNum).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songLabel.setText(songs.get(songNum).getName());

		speedField.setPromptText(Double.toString(Math.floor(volumeSlider.getValue())) + "%");

		speedField.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event)
			{
				try 
				{
					if ((Double.parseDouble(speedField.getText()) / 100) > 2 || (Double.parseDouble(speedField.getText()) / 100) < 0)
					{
						messageBox.setText("Enter a value between 0 and 200 (100 is normal speed).");
						speedField.setText(null);
					}
					else
					{
						mediaPlayer.setRate(Double.parseDouble(speedField.getText()) / 100);
						currentSpeed = Double.parseDouble(speedField.getText()) / 100;
						speedSlider.setValue(mediaPlayer.getRate() * 100);
					}
				}
				catch(NumberFormatException e)
				{
					messageBox.setText("Enter a number value between 0 and 200 (100 is normal speed).");
					speedField.setText(null);
				}
				catch (Exception e)
				{
					messageBox.setText("Error");
					speedField.setText(null);
				}
			}
		});

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() 
		{
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
				volumeLabel.setText(Double.toString(Math.floor(volumeSlider.getValue())) + "%");			
			}
		});

		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) 
			{	
				mediaPlayer.setRate(speedSlider.getValue() * 0.01);
				currentSpeed = speedSlider.getValue() / 100;
				speedLabel.setText(Double.toString(Math.floor(mediaPlayer.getRate() * 100)) + "%");
				speedField.setPromptText(Double.toString(Math.floor(volumeSlider.getValue())) + "%");
			}
		});

		progressBar.setStyle("-fx-accent: black");
	}


    public void playMedia() 
    {
		beginTimer();
		mediaPlayer.setRate(currentSpeed);
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