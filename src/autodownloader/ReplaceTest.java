/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autodownloader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author KICSI
 */
public class ReplaceTest {
   
    public static void main(String[] args){
        String str = "3ó 23p";
        String str2 = "23p";
        String str3 = "2ó";
        int hatravan = 0;
        
        Pattern oraPattern = Pattern.compile("[0-9]+ó");
        Pattern percPattern = Pattern.compile("[0-9]+p");

        Matcher m = oraPattern.matcher(str3);
//         System.out.println(m.find());
//        System.out.println(str.substring(m.start(), m.end()));
        for(int i=0; i<m.groupCount(); i++){
            System.out.println(m.group(i));
        }
        
       
        if(m.find()){
            String oraStr = m.group(0);
            hatravan += Integer.parseInt(oraStr)*60;
        }
        
        
        m = percPattern.matcher(str3);
        System.out.println(m.find());
//         System.out.println(str.substring(m.start(), m.end()));
        if(m.find()){
            String percStr = m.group(0);
            hatravan += Integer.parseInt(percStr);
        }
        
        System.out.println("hatra: "+hatravan);
    }
    
}
