package com.jpassion.myproject.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<DateValid,String>{

	@Override
	public void initialize(DateValid nv){
	
	}
	
	public static boolean isParsable(String input){
	    boolean parsable = true;
	    try{
	        Integer.parseInt(input);
	    }catch(NumberFormatException e){
	        parsable = false;
	    }
	    return parsable;
	}
	
	@Override
	public boolean isValid(String in,ConstraintValidatorContext cxt){
		if(in == null){
			return true;
		}
		
		if(in.length() != 4)
			return false;
		
		String monthString = in.substring(0, 2);
		String dayString = in.substring(2, 4);
		
	    try{
	        int month = Integer.parseInt(monthString);
	        int day = Integer.parseInt(dayString);
	        
	        if(month > 12)
	        	return false;
	        
	        if(day > 31)
	        	return false;
	        
	        return true;
	    }catch(NumberFormatException e){
	        return false;
	    }
	}
}