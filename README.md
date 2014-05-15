ftlv
====

Factored Time-Lapse Video

My partner is Feng Wang

----------------------------------------------------------------------
User Manual
This FTLV project is running on Processing. Instead of using original Processing, we build it in Eclipse.
The final code is on https://github.com/robinbattle/ftlv

Brief Introduction: 
There are two parts of our project:
1. FTLV Maker: A program inputs a folder path which contains a set of photos, outputs a folder which contains the compression files of video.
¡¡¡¡The output file will be saved under the same parent folder of input path.
¡¡¡¡(2) Compression will run in backend. Suggest to run this in command line, so you can see the verbose details.
2. FTLV Viewer: A program inputs a folder path which is the folder produces by program 1. 
¡¡¡¡Logo: If you want to add logo to the video, go to videoFolder/fixedImage, edit on sky.jpg or Sun.jpg, then re-run viewer. (The change is not invertible)

Usage:
In order to run our code, There are two ways:
1. Use Runnable Jar Files:
2. Use Eclipse
(1) Import FTLV Maker in
¡¡¡¡¡°Source code\Maker\FactoredTimeLapseVideoMaker¡±
¡¡¡¡Import FTLV Viewer in 
¡¡¡¡¡°Source code\Viewer\FactoredTimeLapseVideoViewer¡±
(2) Make sure dependency library (processing-core-1.0.3__0.1.0.jar) is linked correctly. It can be find in 
¡¡¡¡¡°Source code\Maker\FactoredTimeLapseVideoMaker\lib¡± &
¡¡¡¡¡°Source code\Viewer\FactoredTimeLapseVideoViewer\lib¡±
(3) Compile and run the code