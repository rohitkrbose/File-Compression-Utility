/** This program will convert the contents of out_enc.dat to a binary string
	in a temporary file called bin.tmp and then decode it using Dictionary.dic
	by checking what substring of bin.tmp matches with the strings in the dictionary. */ 


import java.io.*;
import java.util.*;
import java.lang.*;

class Decode
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

		//closing input streams; free memory
		br.close();
		in.close();

    }

    //function to convert ASCII value of character to x-bit string

    public String char_to_x_bit (char ch, int x)
	{

		String strbin; //stores binary value of key of character
		strbin = Integer.toBinaryString((int)ch); //converting to binary and storing in string
		while (strbin.length() != x)
			strbin = "0" + strbin; //until it is not x bit long
		return strbin; //return x-bit string

	}


    public void createBinString () throws IOException
    {

    	//Open encoded file and convert its contents to binary
    	FileInputStream fstream = new FileInputStream("out_enc.dat");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		//Open extra.bit file to know no. of extra bits used in encoding
		FileInputStream fs = new FileInputStream("extra.bit");
		BufferedReader in = new BufferedReader(new InputStreamReader(fs));

		PrintWriter out = new PrintWriter ("bin.tmp"); //stream to write to temporary binary string

		int x, ex;
		char ch = '\0';

		ex = Integer.parseInt(in.readLine()); //no. of extra bits

		while ( (x = br.read() ) != -1) //if not EOF
		{
			ch = (char)x;
			br.mark( 1 ); //marking current position and telling it that only character after mark will be read
			if (br.read() != -1)
				out.write ( char_to_x_bit(ch, 8) );
			else
				out.write (char_to_x_bit (ch, 8 - ex));
			br.reset(); //reset to marked position
		}

		//closing IO streams
		br.close();
		in.close();
		out.close();

    }

    //function to check character corresponding to binary string

	public int lookUp (String chk)
	{

		int i, index;
		index = -1;
		for (i = 0; i < count; i++)
		{
			if ( chk.equals(code[i]) )
			{
				index = i;
				break; //match found
			}
		}
		return index; //return index of appropriate character

	}

	//function to use dictionary and convert binary string to characters

	public void convToDecodedMsg () throws IOException
	{

		//Open temporary binary string file and convert its contents to character
    	FileInputStream fstream = new FileInputStream("bin.tmp");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		PrintWriter out = new PrintWriter ("out_dec.dat"); //stream to write encoded contents to this file

		int i, x, ret;
		char ch;
		String binWord = "";
		while ( ( x = br.read()) != -1) //if not EOF
		{
			ch = (char)x;
			binWord += ch;
			ret = lookUp(binWord); //check if character corresponding to binWord is available
			if (ret == -1) //if word is not found
				continue;
			else
			{
				out.write ( data[ret] ); //if match is found, write character
				binWord = "";
			}
		}

		//closing IO streams
		br.close();
		out.close();

		//delete temporary file that was created
		File file = new File("bin.tmp");
        file.delete();

	}

	public static void main (String args[]) throws IOException
	{

		Decode ob = new Decode (); //creating object of class
		//calling functions to decode
		ob.load_dictionary();
		ob.createBinString();
		ob.convToDecodedMsg();

	}

}
