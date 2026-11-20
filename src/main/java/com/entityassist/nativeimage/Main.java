package com.entityassist.nativeimage;

import com.entityassist.converters.LocalDateAttributeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class Main
{
    private Main()
    {
    }

    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Usage: entityassist <YYYY-MM-DD>");
            System.exit(2);
            return;
        }

        LocalDate input;
        try
        {
            input = LocalDate.parse(args[0]);
        }
        catch (DateTimeParseException e)
        {
            System.err.println("Invalid ISO date: " + args[0]);
            System.exit(2);
            return;
        }

        var converter = new LocalDateAttributeConverter();
        var sqlDate = converter.convertToDatabaseColumn(input);
        var result = converter.convertToEntityAttribute(sqlDate);
        if (!input.equals(result))
        {
            throw new IllegalStateException("EntityAssist date conversion changed " + input + " to " + result);
        }
        System.out.println(result);
    }
}
