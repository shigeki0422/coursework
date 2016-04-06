// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.jpassion.myproject.web;

import com.jpassion.myproject.mydomain.Flight;
import com.jpassion.myproject.mydomain.Itinerary;
import com.jpassion.myproject.mydomain.Users;
import com.jpassion.myproject.web.ApplicationConversionServiceFactoryBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    declare @type: ApplicationConversionServiceFactoryBean: @Configurable;
    
    public Converter<Flight, String> ApplicationConversionServiceFactoryBean.getFlightToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.jpassion.myproject.mydomain.Flight, java.lang.String>() {
            public String convert(Flight flight) {
                return new StringBuilder().append(flight.getAirlinecode()).append(' ').append(flight.getAirlinename()).append(' ').append(flight.getFlightnumber()).append(' ').append(flight.getDepartureday()).toString();
            }
        };
    }
    
    public Converter<Long, Flight> ApplicationConversionServiceFactoryBean.getIdToFlightConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.jpassion.myproject.mydomain.Flight>() {
            public com.jpassion.myproject.mydomain.Flight convert(java.lang.Long id) {
                return Flight.findFlight(id);
            }
        };
    }
    
    public Converter<String, Flight> ApplicationConversionServiceFactoryBean.getStringToFlightConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.jpassion.myproject.mydomain.Flight>() {
            public com.jpassion.myproject.mydomain.Flight convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Flight.class);
            }
        };
    }
    
    public Converter<Itinerary, String> ApplicationConversionServiceFactoryBean.getItineraryToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.jpassion.myproject.mydomain.Itinerary, java.lang.String>() {
            public String convert(Itinerary itinerary) {
                return new StringBuilder().append(itinerary.getCreated()).append(' ').append(itinerary.getStatus()).append(' ').append(itinerary.getUsername()).append(' ').append(itinerary.getNumberofpassengers()).toString();
            }
        };
    }
    
    public Converter<Long, Itinerary> ApplicationConversionServiceFactoryBean.getIdToItineraryConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.jpassion.myproject.mydomain.Itinerary>() {
            public com.jpassion.myproject.mydomain.Itinerary convert(java.lang.Long id) {
                return Itinerary.findItinerary(id);
            }
        };
    }
    
    public Converter<String, Itinerary> ApplicationConversionServiceFactoryBean.getStringToItineraryConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.jpassion.myproject.mydomain.Itinerary>() {
            public com.jpassion.myproject.mydomain.Itinerary convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Itinerary.class);
            }
        };
    }
    
    public Converter<Users, String> ApplicationConversionServiceFactoryBean.getUsersToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.jpassion.myproject.mydomain.Users, java.lang.String>() {
            public String convert(Users users) {
                return new StringBuilder().append(users.getPassword()).append(' ').append(users.getName()).append(' ').append(users.getAddress()).append(' ').append(users.getEmail()).toString();
            }
        };
    }
    
    public Converter<Long, Users> ApplicationConversionServiceFactoryBean.getIdToUsersConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.jpassion.myproject.mydomain.Users>() {
            public com.jpassion.myproject.mydomain.Users convert(java.lang.Long id) {
                return Users.findUsers(id);
            }
        };
    }
    
    public Converter<String, Users> ApplicationConversionServiceFactoryBean.getStringToUsersConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.jpassion.myproject.mydomain.Users>() {
            public com.jpassion.myproject.mydomain.Users convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Users.class);
            }
        };
    }
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getFlightToStringConverter());
        registry.addConverter(getIdToFlightConverter());
        registry.addConverter(getStringToFlightConverter());
        registry.addConverter(getItineraryToStringConverter());
        registry.addConverter(getIdToItineraryConverter());
        registry.addConverter(getStringToItineraryConverter());
        registry.addConverter(getUsersToStringConverter());
        registry.addConverter(getIdToUsersConverter());
        registry.addConverter(getStringToUsersConverter());
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
}