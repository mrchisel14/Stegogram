package com.cen.steganography;

import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;


/**
@author Team [Redacted]
Encrypts and encodes text message into image file.
Decodes and decrypts text from image file.
*/


public class CryptoEngine 
{

	
	/**
	Encodes a text String into an image file.
	
	Saves the output image as a png file.
	
	@param password for encrypting text String.
	@param text String to be hidden inside image.
	@param file pathway for image in which text will be hidden.
	
	@return the File Path of the saved image
	*/
	public static Bitmap generateStegogram(String password, String message, Bitmap original) throws IndexOutOfBoundsException	
	{
		
		//Generate an editable copy of the image.
		Bitmap encodedImage = original.copy(Bitmap.Config.ARGB_8888, true);	//is editable
		int width = encodedImage.width();
		int height = encodedImage.height();
		
		//release original image resources
		original.recycle();
		
		/*add marker characters to begining and end of string*/
		String terminal = Character.toString('\u03D1');
		message = terminal.concat(message);
		message = message.concat(terminal);
		
		/*Image file must be of sufficient size to hold the message,
		start code, and end code. This verifies that the image is of
		adequate size*/
		// time 8 because each in will be split across four pixels.
		if(message.length() * 4 > (height * width)
		{throw new IndexOutOfBoundsException("Input image filesize too small to hold message String");}
		
		
		/*convert message_data into four bit values*/
		/*so that each bit per value can be used as a lowest order ARBG bit*/
		int[] message_data = new int[message.length() * 4];
		for(int i = 0; i < message.length(); ++i)
		{
			message_data[4 * i] = message.charAt(i & 0x0000F000 >> 12);
			message_data[1 + 4 * i] = message.charAt(i & 0x00000F00 >> 8);
			message_data[2 + 4 * i] = message.charAt(i & 0x000000F0 >> 4);
			message_data[3 + 4 * i] = message.charAt(i & 0x0000000F);
			
		}
		
		
		
		/*encryption goes here*/
		
		/*instert message into image*/
		
		//Convert image pizels into an int array
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		//loop through message and pixel arrays in parral
		//Each color has four 8 bit components
		//Each nibble in message_data is spread out across the 
		//lower order bits of the Color components.
		
		for(int i = 0; i < message_data.length(); ++i)
		{
			int pixel_color = pixels[i];
			
			//insert bit 3 into alpha
			int alpha = Color.alpha(pixel_color);
			int alpha_insert = (message_data[i] & 0x8) >>> 3; 
			alpha = alpha | alpha_insert;
			
			
			//insert bit 2 into red
			int red = Color.red(pixel_color);
			int red_insert = (message_data[i] & 0x4) >>> 2; 
			red = red | red_insert;
			
			
			//insert bit 1 into green
			int green = Color.green(pixel_color);
			int green_insert = (message_data[i] & 0x2) >>> 1;
			green = green | green_insert;
			
			//insert bit 0 into blue
			int blue = Color.blue(pixel_color);
			int blue_insert = (message_data[i] & 0x2) >>> 1;
			blue = blue | blue_insert;
			
			//write final value to pixels
			pixels[i] = Color.argb(alpha, red, green, blue);
		}
		
		
		return encodedImage.setPixels(pixels, 0, width, 0, 0, width, height);
		
	}//end method receiveStegogram


	/**
	Decodes a text String from an image file
	
	@param file pathwy for image from which text will be decoded.
	
	@return decoded and decrypted text string.
	*/
	public static String receiveStegogram(String imgPath)
	{
	
	
	
	
	
	
	}//end method receiveStegogram




}//end class CryptoEngine