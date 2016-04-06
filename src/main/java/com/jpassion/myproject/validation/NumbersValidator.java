package com.jpassion.myproject.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NumbersValidator implements ConstraintValidator<NumbersValid,String>{

	@Override
	public void initialize(NumbersValid nv){
	
	}
	
	@Override
	public boolean isValid(String in,ConstraintValidatorContext cxt){
		if(in == null){
			return true;
		}

		if(in.length() != 16)
			return false;
		
	    try{
	        Long.parseLong(in);
	        return true;
	    }catch(NumberFormatException e){
	        return false;
	    }
	}
}