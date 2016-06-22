/** This program will use Dictionary.dic to encode the contents of source.dat
	in a binary string that will be temporarily stored in a file called bin.tmp,
	which is deleted after execution of the program. The binary string in bin.tmp
	is grouped in sets of eight and stored in ASCII characters in out_enc.dat . */


import java.io.*;
import java.util.*;
import java.lang.*;

public class Encode
{
	
	char[] data; //distinct character array imported from dictionary
	String[] code; //code assigned to each character by Huffman coding
	int count = 0; //no. of distinct characters in data[]


	//function to load dictionary into memory:

	public void load_dictionary () throws IOException
	{

		// Open the file to count no. of distinct characters
		FileInputStream fstream = new FileInputStream("dictionary.dic");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		int i;

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   
		{
			if (strLine.equals("")==true)
			{
				continue;
			}
			else
			{
				count++; //counting the number of distinct characters
			}
		}

		data = new char [count]; //initializng distinct character array
		code = new String [count]; //initializing Huffman code array

		//Open the file again, but for storing in array
		FileInputStream fs = new FileInputStream("dictionary.dic");
		BufferedReader in = new BufferedReader(new InputStreamReader(fs));

		//Read File By Line
		i=0;
		while ((strLine = in.readLine()) != null) //if a line is actually available  
		{
			if (strLine.equals("")) //empty line: means new line character
			{
				strLine = in.readLine(); //go to next line, as code of new line character is in new line
				data[i] = '\n'; //newline character
				code[i] = strLine;
			}
			else
			{
				data[i] = strLine.charAt(0); //first character of line is the character itself
				code[i] = strLine.substring(1, strLine.length()); //remaining part is the Huffman code for that character
			}
			i++; //incrementing index
		}

		//closing IO streams
		br.close();
		in.close();

    }


    //function to look for matching character in dictionary

    public int lookUp (char ch)
    {
    	int i;
    	for (i = 0; i < count; i++)
    	{
    		if (data[i] == ch)
    		{
    			break;
    		}
    	}
    	return i; //return index of matching character
    }


    //function to check if string is binary or not

    public boolean isBinary (String s)
    {
    	int i;
    	for (i = 0; i < s.length(); i++)
    	{
    		if (!(s.charAt(i)=='0' || s.charAt(i)=='1'))
    			return false;
    	}
    	return true;
    }


    //function that converts binary string to compressed message

    public void convToEncodedMsg () throws IOException
    {

    	//Open temporary file to read binary string
    	FileInputStream fstream = new FileInputStream("bin.tmp");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		//Write files
		PrintWriter out_enc = new PrintWriter ("out_enc.dat"); //encoded message
		PrintWriter out_bit = new PrintWriter ("extra.bit"); //extra bit required is stored in this file

    	int i, x, flag, extra;
    	extra = 0; //if the number of bits in bin.tmp is not a multiple of 8, extra bits are added to make it a multiple of 8.
    	char ch;
    	String temp = ""; //8-bit string

    	while ( (x = br.read() ) != -1 )
    	{
    		ch = (char)x;
    		br.mark(1); //marking current position of stream
    		temp += ch; //creating 8-bit string
    		flag = br.read();
    		if ( temp.length() == 8 || flag == -1 )
    		{
    			if (isBinary (temp)) //if binary string
    			{
    				while ( temp.length() != 8 )
    				{
    					extra ++; //extra bits needed to form 8-bit string
    					temp = "0" + temp; //if not 8 bit
    				}
    				ch = bits_to_char (temp);
    				out_enc.write( Character.toString( bits_to_char (temp) ) ); //write apparently nonsense character
    				temp = "";
    			}		
    		}
    		br.reset(); //since flag reads output stream, we need to reset it
    	}

		out_bit.write(Integer.toString(extra)); //writing to output file extra.bit

		//closing IO Streams
		out_enc.close();
		out_bit.close();
		br.close();	

		//delete temporary file that was created
		File file = new File("bin.tmp");
        file.delete();

    }


    //convert 8-bit string to corresponding ASCII character

    public char bits_to_char (String str)
	{

		//assumes that string passed is binary in nature!
		char c;
		int dec;
		dec = Integer.parseInt(str, 2);
		c = (char)dec;
		return c;

	}


    public void binConv () throws IOException
    {

    	// Open the source file which is to be encoded
		FileInputStream fstream = new FileInputStream("source.dat");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String temp;
		char ch;
		int x, i;
		boolean flag; //to check if code is actually found in code array

		PrintWriter out = new PrintWriter ("bin.tmp"); //stream to write to temporary binary string
		while ( (x = br.read() ) != -1) //if not EOF
		{
			flag = false;
			ch = (char)x;
			//validate if all charaacters of source are there in dictionary
			for (i = 0; i < count; i++)
			{
				if (ch == data[i])
				{
					flag = true; //if code matches
					break;
				}
			}
			try
			{
				if (flag)
				{
					temp = code[lookUp(ch)]; //dictionary corresponding binary string
					out.write( temp ); //write binary string to temporary file
				}
			}
			catch (Exception e) {}
		}

		//closing IO streams
		out.close();
		br.close();

    }
    

    public static void main (String args[]) throws IOException
    {

    	Encode ob = new Encode(); //creating object of class
    	//calling functions to encode
    	ob.load_dictionary();
    	ob.binConv();
    	ob.convToEncodedMsg();

    }

}