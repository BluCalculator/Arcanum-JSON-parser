/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.arcanumjsonparser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author BluCalculator
 */
public class ArcanumJSONParser {
    public static void main(String[] args) {
        if (args.length==0) {
            System.out.println("Usage: ArcanumJSONParser resources");
            return;
        }
        System.out.println("running with "+args.length+" arguments, first is:"+args[0]);
        if (args[0].toLowerCase().equals("resources")) {
            try {
                URL url = new URL("http://www.lerpinglemur.com/arcanum/siversgate/data/resources.json");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                
                ArrayList<Resource> rss= new ArrayList<Resource>();
                Resource res = new Resource();//first res object should be empty. Just roll with it I'm too lazy to fix this.
                res.setName("supposedly empty entry - ignore this line");
                Pattern id=Pattern.compile("id\\\"\\:\\\"(\\w*)\\\"");//                  id\"\:\"(\w*)\"
                Pattern name=Pattern.compile("name\\\"\\:\\\"(\\w*)\\\"");//                name\"\:\"(\w*)\"
                Pattern desc=Pattern.compile("desc\\\"\\:\\\"([\\w\\s]*)\"");//             desc\\\"\\:\\\"([\\w\\s]*)\"    
                Pattern mod=Pattern.compile("\\\"(\\w*\\.[\\w.]*)\\\":\\\"?([\\d.%]+)");//  \"(\w*\.[\w.]*)\":\"?([\d.%]+)
                Matcher m;
                int found=0;
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    m=id.matcher(inputLine);
                    if (m.find()) {
                        System.out.println("Found " + ++found);
                        rss.add(res);
                        res=new Resource();
                        res.setName(m.group(1));
                        continue;
                    }
                    m=name.matcher(inputLine);
                    if (m.find()) {
                        res.setName(m.group(1));
                        continue;
                    }
                    m=desc.matcher(inputLine);
                    if (m.find()) {
                        res.setDesc(m.group(1));
                        continue;
                    }
                    m=mod.matcher(inputLine);
                    if (m.find()) {
                        if (m.group(1).equals("max")) continue;
                        res.addMod(m.group(1),m.group(2));
                        continue;
                    }
                    
                }
                in.close();
                
                for (Resource incRes : rss) {
                    System.out.println(incRes.toString());
                }
                
                
                
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(ArcanumJSONParser.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Bad URL");
                return;
            } catch (IOException ex) {
                Logger.getLogger(ArcanumJSONParser.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Failed to read");
                return;
            }
        } else {
            System.out.println("Usage: ArcanumJSONParser <URL>");
            System.out.println("URL must start with http");
        }
    }
    public static boolean isURL(String str) {return true;}//TODO: write and/or import this
}
class Resource {
    private String name;
    private String desc;

    private ArrayList<String> mods;
    public Resource() {
        mods = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getMods() {
        if (mods.size()==0) return "";
        StringBuilder output=new StringBuilder();
        for (String mod : mods) {
            output.append(mod);
            output.append(" : ");
        }
        return output.toString();
    }
    
    
    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    void addMod(String str, String num) {
        if (str.substring(str.length()-4, str.length()).equals(".max")) {
            mods.add("max "+str.substring(0,str.length()-4)+"+= "+num+" each\n");
        } else if (str.substring(str.length()-5, str.length()).equals(".rate")) {
            mods.add(str.substring(0,str.length()-5)+"+= "+num+"/s each\n");
        } else {
            mods.add(str+"+= "+num+" each\n");
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append("\n  ");
        sb.append(desc);
        for (String mod : mods) {
            sb.append("\n    ");
            sb.append(mod);
        }
        return sb.toString();
    }
}
