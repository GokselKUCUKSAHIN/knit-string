package com.jellybeanci.numberPi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class Number
{

    private ArrayList<Character> digits = new ArrayList<>();
    public int count = 0;
    public Number(Reader rdr)
    {
        extractData(rdr);
        count = digits.size();
    }

    private void extractData(Reader rdr)
    {
        try (BufferedReader br = new BufferedReader(rdr))
        {
            String line;
            while ((line = br.readLine()) != null) // if not Empty
            {
                line = line.trim(); // For get rid of White Spaces
                for (int i = 0; i < line.length(); i++)
                {
                    char chr = line.charAt(i);
                    if (isNumber(chr))
                    {
                        digits.add(chr);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean isNumber(char chr)
    {
        return (short) chr <= 57 && (short) chr >= 48;
    }

    public ArrayList<Character> getDigits()
    {
        return this.digits;
    }
}
