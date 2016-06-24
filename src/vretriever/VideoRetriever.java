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
    private final String apiCall = "https://www.googleapis.com/youtube/v3/playlistItems?playlistId=%s&key=AIzaSyDpUsDifb_EkkHWaMXk1pUprv9Qdp3vlII&part=snippet&maxResults=50";
    private final String youtubeDlCommand = "start /B youtube-dl --extract-audio --audio-format mp3 https://www.youtube.com/watch?v=";
    
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
            String result=readApiCall(apiCall,idList);        
            writeResultingScript(result);
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

        outputFilePath= Paths.get(outputPathDir,idList+".bat");
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

    private void writeResultingScript(String result) throws IOException
    {
        
        ArrayList<String> resultLines=new ArrayList<>();
            
        resultLines.add("cd "+outputPathDir);
        
        for (String videoId:listOfIds(result))
        {
            resultLines.add(youtubeDlCommand+videoId);
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
            listToReturn.addAll(listOfIds(readApiCall(apiCall,idList,nextPage)));
            
            return listToReturn;
        }
    }
        
}