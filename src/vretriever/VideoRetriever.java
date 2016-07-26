package vretriever;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class VideoRetriever extends Thread
{
    private final String idList;
    private final String outputPathDir="output_scripts";
    private final String apiCallPlayList = "https://www.googleapis.com/youtube/v3/playlistItems?playlistId=%s&key=AIzaSyDpUsDifb_EkkHWaMXk1pUprv9Qdp3vlII&part=snippet&maxResults=50";
    private final String apiCallUserList = "https://www.googleapis.com/youtube/v3/playlistItems?playlistId=%s&key=AIzaSyDpUsDifb_EkkHWaMXk1pUprv9Qdp3vlII&part=snippet&maxResults=50";
    private final String youtubeDlCommandWindows = "start /B youtube-dl --extract-audio --audio-format mp3 https://www.youtube.com/watch?v=";
    private final String youtubeDlCommandLinux = "youtube-dl --extract-audio --audio-format mp3 https://www.youtube.com/watch?v=%s &";
    
    private Path outputFilePath;
    
    public VideoRetriever(String listID)
    {        
        idList=listID;
    }

    @Override
    public void run()
    {        
        createDirectory();       	

        try 
        {
            String result=readApiCall(apiCallPlayList,idList);        
            writeResultingScript(result, System.getProperty("os.name").equals("Linux")?TypeOs.LINUX:TypeOs.WINDOWS);
        }
        catch (IOException e)
        {
          System.out.println(e.getMessage());
        }
    }

    private void createDirectory()
    {
        
        File newDir = new File(outputPathDir);    

        if(!newDir.exists())            
            newDir.mkdir();

        outputFilePath= Paths.get(outputPathDir,idList+(System.getProperty("os.name").equals("Linux")?".sh":".bat"));
    }

    private String readApiCall(String call, String ids) throws MalformedURLException, IOException
    {
        
        String cadenaURL= String.format(call, idList);

        URL url = new URL(cadenaURL);

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        String result="";

        while ((inputLine = in.readLine()) != null)
            result+=inputLine+"\n";

        in.close();

        return result;
    }
    
    private String readApiCall(String call, String ids, String nextPageToken) throws MalformedURLException, IOException
    {
        
        String cadenaURL= String.format(call, idList)+"&pageToken="+nextPageToken;

        URL url = new URL(cadenaURL);

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        String result="";

        while ((inputLine = in.readLine()) != null)
            result+=inputLine+"\n";

        in.close();

        return result;
    }

    private void writeResultingScript(String result, TypeOs type) throws IOException
    {
        
        ArrayList<String> resultLines=new ArrayList<>();
            
        resultLines.add("cd "+outputPathDir);
        
        for (String videoId:listOfIds(result))
        {
            resultLines.add(type.equals(TypeOs.WINDOWS)?(youtubeDlCommandWindows+videoId):(String.format(youtubeDlCommandLinux,videoId)));
        }
        
        Files.write(outputFilePath, resultLines, Charset.forName("UTF-8"));
        
    }
    
    private ArrayList<String> listOfIds(String json) throws IOException
    {
        ArrayList<String> listToReturn = new ArrayList<>();
        
        String nextPage="";
               
        for(String line:json.split("\n"))
        {
            if(line.contains("nextPageToken"))
                nextPage=line.split(":")[1].replace(",","").replace("\"","").trim();
            
            if(line.contains("videoId"))
            {
                String aux= line.split(":")[1].replace("\"","");
                listToReturn.add(aux.trim());
            }            
        }
        
        if(listToReturn.size()!=50)        
            return listToReturn;
        
        else
        {
            listToReturn.addAll(listOfIds(readApiCall(apiCallPlayList,idList,nextPage)));
            
            return listToReturn;
        }
    }
        
}