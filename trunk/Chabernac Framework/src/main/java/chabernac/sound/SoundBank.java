/*
 * Created on 7-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.sound;

import java.applet.AudioClip;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import chabernac.utils.Debug;

public class SoundBank {
	private static Hashtable mySounds = new Hashtable();
	private static Random myRandomizer = new Random();
	
	public static void loadSoundBank(String aLocation){
		//Class theClass = mySounds.getClass();
		ClassLoader theLoader = ClassLoader.getSystemClassLoader();
		InputStream theInputStream = theLoader.getResourceAsStream(aLocation);
		BufferedReader theReader = new BufferedReader(new InputStreamReader(theInputStream));
		String theLine = null;
		try{
			while((theLine = theReader.readLine()) != null){
				StringTokenizer theTokenizer = new StringTokenizer(theLine);
				if(theTokenizer.countTokens() == 2){
					addSound(theTokenizer.nextToken(), theTokenizer.nextToken());
				}
			}
		}catch(IOException e){
			Debug.log(SoundBank.class,"could not load soundbank at location: " + aLocation, e);
		}
	}
	
	public static void addSound(String aKey, String aLocation){
		try{
			AudioClip theClip = (AudioClip)(ClassLoader.getSystemClassLoader().getResource(aLocation).getContent());
			if(!mySounds.containsKey(aKey)) mySounds.put(aKey, new Vector());
			Vector theSoundVector = (Vector)mySounds.get(aKey);
			theSoundVector.addElement(theClip);
		}catch(IOException e){
			Debug.log(SoundBank.class, "No audio clip found at: " + aLocation);
		}catch(Exception e){
			Debug.log(SoundBank.class, "No audio clip found at: " + aLocation);
		}
	}
	
	private static AudioClip getAudioClip(String aKey){
		if(!mySounds.containsKey(aKey)) return null;
		Vector theSoundVector = (Vector)mySounds.get(aKey);
		int size = theSoundVector.size(); 
		if(size == 0) return null;
		int which = Math.abs(myRandomizer.nextInt()) % size;
		return (AudioClip)theSoundVector.elementAt(which);
	}
	
	public static void play(String aSound){
		AudioClip theClip = getAudioClip(aSound);
		if(theClip != null) theClip.play();
	}
	
	public static void loop(String aSound){
		AudioClip theClip = getAudioClip(aSound);
		if(theClip != null) theClip.loop();
	}
	
	public static void stop(String aSound){
		AudioClip theClip = getAudioClip(aSound);
		if(theClip != null) theClip.stop();
	}
}
