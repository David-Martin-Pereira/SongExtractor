/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vretriever;

/**
 *
 * @author klaithal-lubuntu
 */
public class ScriptExecutor
{
    TypeOs type;
    String path;
    
    public ScriptExecutor(TypeOs type, String path)
    {
        this.type=type;                
        this.path=path;
    }
    
    public void execute()
    {                
        
        if(TypeOs.WINDOWS.equals(type))
        {
            
            
            try
            {
                Runtime.getRuntime().exec("powershell /c start "+path+".bat");
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }  
        }
        else
        {            
            
            try
            {
                Runtime.getRuntime().exec("sh "+path+".sh");
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }  
        }
    }
    
}
