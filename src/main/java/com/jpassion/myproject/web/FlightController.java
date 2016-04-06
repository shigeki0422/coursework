package com.jpassion.myproject.web;
import com.jpassion.myproject.mydomain.Flight;
import com.jpassion.myproject.mydomain.Itinerary;
import com.jpassion.myproject.mydomain.Users;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/flights")
@Controller
@RooWebScaffold(path = "flights", formBackingObject = Flight.class)
public class FlightController {

    /**
     * Create new flight page which happens after user input
     * @param flight
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(params = {"create"}, method = RequestMethod.POST, produces = "text/html")
    public String create_new(@Valid Flight flight, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {

        
    	System.out.println("a");
    	if (bindingResult.hasErrors()) {
        	System.out.println("b");
            populateEditForm(uiModel, flight);
            return "flights/create_new";
        }

    	System.out.println("c");
    	{
            String[] splited = flight.getDeparturetime().split(":");
            if (splited.length == 2) {
                int hours = Integer.parseInt(splited[0]);
                int minutes = Integer.parseInt(splited[1]);
                flight.getDepartureday().setHours(hours);
                flight.getDepartureday().setMinutes(minutes);
            }
        }
        {
            String[] splited = flight.getArrivaltime().split(":");
            if (splited.length == 2) {
                int hours = Integer.parseInt(splited[0]);
                int minutes = Integer.parseInt(splited[1]);
                flight.getArrivalday().setHours(hours);
                flight.getArrivalday().setMinutes(minutes);
            }
        }

    	System.out.println(flight.getDepartureday());
    	System.out.println(flight.getDeparturetime());
        uiModel.asMap().clear();
        flight.persist();
        return "redirect:/flights/" + encodeUrlPathSegment(flight.getId().toString(), httpServletRequest);
    }
    
    /**
     * Create new flight page
     * @param uiModel
     * @return
     */
    @RequestMapping(params = {"create", "form"}, produces = "text/html")
    public String createForm_new(Model uiModel) {
    	populateEditForm(uiModel, new Flight());
        return "flights/create_new";
    }
    

    
    /**
     * List flights page
     * @param page
     * @param size
     * @param sortFieldName
     * @param sortOrder
     * @param uiModel
     * @return
     */
    @RequestMapping(params = {"list_all"}, produces = "text/html")
    public String list_all(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("flights", Flight.findFlightEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Flight.countFlights() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("flights", Flight.findAllFlights(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "flights/list_all";
    }

    
    /**
     * Delete flight page
     * @param id
     * @param uiModel
     * @return
     */
    @RequestMapping(params = {"delete_flight"}, produces = "text/html")
    public String delete_flight(@RequestParam("id") Long id, Model uiModel) {
    	
    
        Flight flight = Flight.findFlight(id);
        if(flight != null)
        {
        	List<Itinerary> itinerarys = flight.findItinerys(true);
        	itinerarys.addAll(flight.findItinerys(false));
        	for(Itinerary itinerary : itinerarys){
                itinerary.setStatus("cancelled");
                itinerary.merge();
        	}
        	flight.setCancelled(true);
        	flight.merge();
            //uiModel.asMap().clear();
        }
        return "redirect:/flights?list_all";
    }
    
}
