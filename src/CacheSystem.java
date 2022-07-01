import java.util.*;
import java.io.*;
import java.math.*;

public class CacheSystem 
{
	static int totalAccess=0;
	static int totalHits=0;
	static int totalInstructionAddress=0;
	static int totalClockCycles=0;
	static String[] data_Size;
    static String[] mainmemory_Data;
    static String[] instruction_address; 
    static HashMap<String, String> cache_memory = new HashMap<String, String>();
    static HashMap<String, String> main_memory = new HashMap<String, String>();
    
    static File file = new File("C:/Users/vikra/Desktop/Vikram_SCU/Fall 21/CA/Project1/output.txt");
    static FileWriter fw;
    static BufferedWriter bw;

   
/*Function obtains data from the given address. 
  Parameters are 32bit address and number of bytes to read.*/
   String fetchData_instrAddress(String address,String number_Bytes) throws IOException{

       String tag_plus_index=address.substring(0,28);
           if(cache_memory.containsKey(tag_plus_index)) // check whether cache contains the address.
           {
               totalHits++;
               totalClockCycles++;
               return this.fetch_ByteData(tag_plus_index+"0000",number_Bytes,cache_memory.get(tag_plus_index),address);
           }
           else // retrieve data from the main memory and store in cache.
           {
               totalClockCycles=totalClockCycles+15;
               String value=main_memory.get(tag_plus_index);
               String indexBits=tag_plus_index.substring(19,28);
               int j=0;
               String index_found="";

               /*Compare Index bit of given address with the index bit of cache line.
                If already present, replace data and change the hashmap key.*/
               for (Map.Entry<String, String> set : cache_memory.entrySet()) {
                   if(set.getKey().substring(19,28).equals(indexBits))
                   {
                       index_found=set.getKey();
                       break;
                   }
                   j++;
               }
               if(j==cache_memory.size())
               {
                   cache_memory.put(tag_plus_index,value);
                   return this.fetch_ByteData(tag_plus_index+"0000",number_Bytes,cache_memory.get(tag_plus_index),address);
               }
               else
               {
                   cache_memory.put(index_found,value);
                   return this.fetch_ByteData(index_found+"0000",number_Bytes,cache_memory.get(index_found),address);
               }
           }
   }

/*Function reads the bytes starting from the given address. 
 Parameters are byte offset address to start reading the data, number of bytes, data present at that cache line and
 address of the first byte in the block */
String fetch_ByteData(String start_Address,String size,String data,String read_Address)
{
   try{
    int i=32;
    
    int byteNumber=Integer.parseInt(read_Address.substring(28,32),2); // to obtain the index
    size=size.substring(0,size.length()); 

    int number_Bytes=Integer.parseInt(size)/2; // no. of bytes to read
    int number_Digits=(number_Bytes)*2; // 1 byte = 2 digits
    totalAccess++;

    if(32-(byteNumber*2)-number_Digits>=0)
    {
        return data.substring(32-(byteNumber*2)-number_Digits,32-(byteNumber*2));

    }
    else // straddling condition
    {
        int temp =Integer.parseInt(start_Address.substring(0,28),2);
        temp=temp+1;
        String add1=String.format("%28s", Integer.toBinaryString(temp)).replaceAll(" ", "0");
       
        return this.fetchData_instrAddress(add1+"0000",String.valueOf(number_Bytes-(16-byteNumber)))+data.substring(0,32-(byteNumber*2));
    }
   }
   catch(IOException e)
   {
        e.printStackTrace();  
   }
    return "";
  
}




//Function to convert the Hexadecimal value to Binary value
String hexToBinary(String hex)
{
  String binary = "";
  hex = hex.toUpperCase();
  HashMap<Character, String> hashMap = new HashMap<Character, String>();

  hashMap.put('0', "0000");
  hashMap.put('1', "0001");
  hashMap.put('2', "0010");
  hashMap.put('3', "0011");
  hashMap.put('4', "0100");
  hashMap.put('5', "0101");
  hashMap.put('6', "0110");
  hashMap.put('7', "0111");
  hashMap.put('8', "1000");
  hashMap.put('9', "1001");
  hashMap.put('A', "1010");
  hashMap.put('B', "1011");
  hashMap.put('C', "1100");
  hashMap.put('D', "1101");
  hashMap.put('E', "1110");
  hashMap.put('F', "1111");

  int i;
  char ch;

  for (i = 0; i < hex.length()-1; i++) {
      
      ch = hex.charAt(i);
      if (hashMap.containsKey(ch))
          binary += hashMap.get(ch);
      else {
          binary = "Invalid Hexadecimal String";
          return binary;
      }
  }

  return binary;
}

public static void main(String args[]) throws IOException{

    if(!file.exists())
    {
        file.createNewFile();
    }
    CacheSystem cs= new CacheSystem();

    //Reading input data
    Scanner addresses= new Scanner(new File("C:/Users/vikra/Desktop/Vikram_SCU/Fall 21/CA/Project1/inst_addr_trace_hex_project_1.txt")).useDelimiter("\n");
    Scanner readNumOfBytes= new Scanner(new File("C:/Users/vikra/Desktop/Vikram_SCU/Fall 21/CA/Project1/inst_data_size_project_1.txt")).useDelimiter("\n");
    Scanner mainMemoryData= new Scanner(new File("C:/Users/vikra/Desktop/Vikram_SCU/Fall 21/CA/Project1/inst_mem_hex_16byte_wide.txt")).useDelimiter("\n");


    List<String> binary_address = new ArrayList<String>();
    List<String> numbytes_instraddr = new ArrayList<String>();
    List<String> data = new ArrayList<String>();


    String temp="";

    //Converting the hexadecimal addresses to binary address.
    while (addresses.hasNext()) {
        temp = addresses.next();
        binary_address.add(cs.hexToBinary(temp));
    }
    addresses.close();

    //Obtaining the number of bytes to read at each address.
    while (readNumOfBytes.hasNext()) {
        temp = readNumOfBytes.next();
        numbytes_instraddr.add(temp.substring(0,temp.length()-1));
    }
    readNumOfBytes.close();

    //Reading 16 byte data.
    while (mainMemoryData.hasNext()) {
        temp = mainMemoryData.next();
        data.add(temp);
    }
    mainMemoryData.close();

    int i=0;

    instruction_address = binary_address.toArray(new String[0]);
    data_Size=numbytes_instraddr.toArray(new String[0]);
    mainmemory_Data=data.toArray(new String[0]);

  FileWriter fWriter = new FileWriter("C:/Users/vikra/Desktop/Vikram_SCU/Fall 21/CA/Project1/output.txt");
  fw=new FileWriter(file.getAbsoluteFile());
  bw= new BufferedWriter(fw);
    
    //Creating mainMemory hashmap of 28 bit address and 128bit data.
    for(String memoryData:mainmemory_Data)
    {
        main_memory.put(String.format("%28s", Integer.toBinaryString(i)).replaceAll(" ", "0"),memoryData);
        i++;
    }
    i=0;


    //Reading the data from the address.
    for(String tarray:instruction_address)
    {
        totalInstructionAddress++;
        bw.write(cs.fetchData_instrAddress(tarray,data_Size[i]));
        bw.write("\n");
        i++;
    }
    
    System.out.println("Total number of clock cycles="+totalClockCycles);
    System.out.println("Total number of Instruction addresses="+totalInstructionAddress);
    System.out.println("Instructions per cycle="+ ((float)totalInstructionAddress/totalClockCycles));
    System.out.println("Total number of hits="+totalHits);
    System.out.println("Total number of cache accesses="+totalAccess);
    System.out.println("Hit ratio="+((float)totalHits/totalAccess));

   


    bw.close();
    fWriter.close();

}
	
	
}
