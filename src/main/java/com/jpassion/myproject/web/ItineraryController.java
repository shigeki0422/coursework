package com.jpassion.myproject.web;
import com.jpassion.myproject.mydomain.Flight;
import com.jpassion.myproject.mydomain.Itinerary;
import com.jpassion.myproject.mydomain.Users;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;

@RequestMapping("/itinerarys")
@Controller
@RooWebScaffold(path = "itinerarys", formBackingObject = Itinerary.class)
public class ItineraryController {
	
    
	/**
	 * Search one way itinerary page
	 * @param uiModel
	 * @return
	 */
	@RequestMapping(params = { "search_one", "form" }, method = RequestMethod.GET)
    public String search_one(Model uiModel) {
        uiModel.addAttribute("flight_departureday_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));

        return "itinerarys/search_oneway";
    }

    /**
     * Search one way itinerary page which happens after user input
     * @param departurelocation
     * @param arrivallocation
     * @param departureday
     * @param departuretime
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @param page
     * @param size
     * @param sortFieldName
     * @param sortOrder
     * @param uiModel
     * @return
     */
    @RequestMapping(params = "find=ByItinerary", method = RequestMethod.GET)
    public String search_one(@RequestParam("departurelocation") String departurelocation, @RequestParam("arrivallocation") String arrivallocation, @RequestParam("departureday") @DateTimeFormat(style = "M-") Date departureday, @RequestParam("departuretime") String departuretime, @RequestParam("numberOfPassengers") Integer numberOfPassengers, @RequestParam("maxNumberOfStops") Integer maxNumberOfStops, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {

    	{
            String[] splited = departuretime.split(":");
            if (splited.length == 2) {
                int hours = Integer.parseInt(splited[0]);
                int minutes = Integer.parseInt(splited[1]);
                departureday.setHours(hours);
                departureday.setMinutes(minutes);
            }
        }
        uiModel.addAttribute("itinerarys", Itinerary.search(departurelocation, arrivallocation, departureday, numberOfPassengers, maxNumberOfStops));
        
        return "itinerarys/foundItineraryList";
    }
    
    /**
     * Search two way itinerary page
     * @param uiModel
     * @return
     */
    @RequestMapping(params = { "search_return", "form" }, method = RequestMethod.GET)
    public String search_return(Model uiModel) {
        uiModel.addAttribute("flight_departureday_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));

        return "itinerarys/search_return";
    }

    /**
     * Search two way itinerary page which happens after user input
     * @param departurelocation
     * @param arrivallocation
     * @param departureday
     * @param departuretime
     * @param arrivalday
     * @param arrivaltime
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @param page
     * @param size
     * @param sortFieldName
     * @param sortOrder
     * @param uiModel
     * @return
     */
    @RequestMapping(params = "find=ByItineraryReturn", method = RequestMethod.GET)
    public String search_return(@RequestParam("departurelocation") String departurelocation, @RequestParam("arrivallocation") String arrivallocation, @RequestParam("departureday") @DateTimeFormat(style = "M-") Date departureday, @RequestParam("departuretime") String departuretime, @RequestParam("arrivalday") @DateTimeFormat(style = "M-") Date arrivalday, @RequestParam("arrivaltime") String arrivaltime, @RequestParam("numberOfPassengers") Integer numberOfPassengers, @RequestParam("maxNumberOfStops") Integer maxNumberOfStops, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        {
            String[] splited = departuretime.split(":");
            if (splited.length == 2) {
                int hours = Integer.parseInt(splited[0]);
                int minutes = Integer.parseInt(splited[1]);
                departureday.setHours(hours);
                departureday.setMinutes(minutes);
            }
        }
        {
            String[] splited = arrivaltime.split(":");
            if (splited.length == 2) {
                int hours = Integer.parseInt(splited[0]);
                int minutes = Integer.parseInt(splited[1]);
                arrivalday.setHours(hours);
                arrivalday.setMinutes(minutes);
            }
        }
        uiModel.addAttribute("itinerarys", Itinerary.search(departurelocation, arrivallocation, departureday, arrivalday, numberOfPassengers, maxNumberOfStops));
        return "itinerarys/foundItineraryListReturn";
    }
    
    /**
     * Reserve one way itinerary page
     * @param departingflights
     * @param numberofpassengers
     * @param business
     * @param uiModel
     * @return
     */
    @RequestMapping(params = { "reserve" }, method = RequestMethod.GET)
    public String reserve(@RequestParam("departingflights") String departingflights, @RequestParam(value = "numberofpassengers") int numberofpassengers, @RequestParam(value = "business", required = false) boolean business, Model uiModel) {
    
    	Users loggedInUser = Users.getLoggedInUser();
    	if(loggedInUser == null)
        	return "redirect:/login";
    	
    	System.out.println("a");
    	ArrayList<Flight> flights = new ArrayList<Flight>();
    	for(String i : departingflights.split(",")){
    		try {
    		      Long id  = Long.parseLong(i);
    		      Flight flight = Flight.findById(id);
    		      if(flight != null)
    		    	  flights.add(flight);
    		} catch (NumberFormatException e) {
    		}
    	}

    	System.out.println("book");
    	Itinerary itinerary = new Itinerary();
    	
    	itinerary.setUsername(loggedInUser.getUsername());
    	itinerary.getFlights().addAll(flights);
    	itinerary.setBusiness(business);
    	itinerary.setNumberofpassengers(numberofpassengers);
    	itinerary.setCreated(new Date());
    	itinerary.persist();
    	
    	System.out.println(flights);
        return "itinerarys/reserved";
    }
    
    /**
     * Reserve two way itinerary page
     * @param departingflights
     * @param returnflights
     * @param numberofpassengers
     * @param business
     * @param uiModel
     * @return
     */
    @RequestMapping(params = { "reserve_return" }, method = RequestMethod.GET)
    public String reserve_return(@RequestParam("departingflights") String departingflights, @RequestParam("returnflights") String returnflights, @RequestParam(value = "numberofpassengers") int numberofpassengers, @RequestParam(value = "business", required = false) boolean business, Model uiModel) {
    
    	Users loggedInUser = Users.getLoggedInUser();
    	if(loggedInUser == null)
        	return "redirect:/login";
    	
    	ArrayList<Flight> flights = new ArrayList<Flight>();
    	for(String i : departingflights.split(",")){
    		try {
    		      Long id  = Long.parseLong(i);
    		      Flight flight = Flight.findById(id);
    		      if(flight != null)
    		    	  flights.add(flight);
    		} catch (NumberFormatException e) {
    		}
    	}

    	ArrayList<Flight> returnFlights = new ArrayList<Flight>();
    	for(String i : returnflights.split(",")){
    		try {
    		      Long id  = Long.parseLong(i);
    		      Flight flight = Flight.findById(id);
    		      if(flight != null)
    		    	  returnFlights.add(flight);
    		} catch (NumberFormatException e) {
    		}
    	}
    	
    	System.out.println("book_return");
    	Itinerary itinerary = new Itinerary();
    	
    	itinerary.setUsername(loggedInUser.getUsername());
    	itinerary.getFlights().addAll(flights);
    	itinerary.getReturnflights().addAll(returnFlights);
    	itinerary.setBusiness(business);
    	itinerary.setNumberofpassengers(numberofpassengers);
    	itinerary.setCreated(new Date());
    	itinerary.persist();
    	
    	System.out.println(flights);
        return "itinerarys/reserved";
    }
    
    /**
     * Booking page
     * @param id
     * @param uiModel
     * @return
     */
    @RequestMapping(params = { "book" }, method = RequestMethod.GET)
    public String book(@RequestParam("id") Long id, Model uiModel) {
    
    	Users loggedInUser = Users.getLoggedInUser();
    	if(loggedInUser == null)
        	return "redirect:/login";
    	
    	if(loggedInUser.getCardNumber() == null)
        	return "redirect:/userses/credit?form";
    	
    	Itinerary itinerary = Itinerary.findItinerary(id);
    	//Not mine
    	if(itinerary == null || !itinerary.getUsername().equals(loggedInUser.getUsername()))
            return "resourceNotFound";
    	//booked already
    	if(itinerary.getStatus().equals("booked"))
            return "bookedAlready";
    	
    	itinerary.setStatus("booked");
    	itinerary.merge();
        return "itinerarys/booked";
    }
    
    /**
     * My list page
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "mylist", produces = "text/html")
    public String mylist(Model uiModel) {
    
	    Users loggedInUser = Users.getLoggedInUser();
        if(loggedInUser == null)
       	 return "redirect:/login";

        uiModel.addAttribute("itinerarys", Itinerary.findValidItinerarysByUsernameEquals(loggedInUser.getUsername()).getResultList());
        
        return "itinerarys/mylist";
    }

    /**
     * Show booked itinerary page
     * @param id
     * @param uiModel
     * @return
     */
    @RequestMapping(params = { "showBooked" }, method = RequestMethod.GET)
    public String showBooked(@RequestParam("id") Long id, Model uiModel) {
    	Users loggedInUser = Users.getLoggedInUser();
    	if(loggedInUser == null)
        	return "redirect:/login";

    	Itinerary itinerary = Itinerary.findItinerary(id);
    	
    	//Not mine
    	if(itinerary == null || !itinerary.getUsername().equals(loggedInUser.getUsername()))
            return "resourceNotFound";
    	//not booked
    	if(!itinerary.getStatus().equals("booked"))
            return "resourceNotFound";
    	
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("itinerary", Itinerary.findItinerary(id));
        uiModel.addAttribute("users", loggedInUser);
        uiModel.addAttribute("itemId", id);
        return "itinerarys/showBookedItinerary";
    }
}
