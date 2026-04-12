from pytubefix import YouTube

def Download(link):
    youtubeObject = YouTube(link)
    youtubeObject = youtubeObject.streams.get_audio_only()
    try:
        youtubeObject.download(output_path="C:/Users/unais/OneDrive/Documents/Java Programs/MusicPlayer/music")
    except:
        print("An error has occurred")
    print("Download is completed successfully")


#link = input("Enter the YouTube video URL: ")
#Download(link)

Download("https://www.youtube.com/watch?v=EGBK-j2Us5s")