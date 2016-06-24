/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vretriever;

import java.util.Scanner;

/**
 *
 * @author dmartin
 */
public class ConsoleTest {
    
    public static void main (String args[])
    {       
        new VideoRetriever(args[0]).start();       
        
        Scanner lector = new Scanner(System.in);
        
        System.out.print("Do you want to run the resulting script? (s/n): ");
        
        String ok = lector.nextLine().toLowerCase();
        
        if(ok.equals("s"))       
            try
            {
                Runtime.getRuntime().exec("powershell /c start output_scripts/"+args[0]+".bat");
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }        
        
        System.out.println("done!");
        
    }
    
}
