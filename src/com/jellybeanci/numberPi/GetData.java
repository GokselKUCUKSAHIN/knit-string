package com.jellybeanci.numberPi;

import java.io.*;

public class GetData
{
    public static Number getFile(String path) throws IOException
    {
        return new Number(getReader(path));
    }
    public static Reader getReader(String path) throws IOException
    {
        File file = new File(path);
        if(file.isDirectory() || !file.exists())
        {
            throw new FileNotFoundException("File Not Found");
        }
        else
        {
            return new FileReader(file);
        }
    }
}
