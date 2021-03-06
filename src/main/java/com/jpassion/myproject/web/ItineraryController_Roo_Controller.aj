// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.jpassion.myproject.web;

import com.jpassion.myproject.mydomain.Flight;
import com.jpassion.myproject.mydomain.Itinerary;
import com.jpassion.myproject.web.ItineraryController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect ItineraryController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String ItineraryController.create(@Valid Itinerary itinerary, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, itinerary);
            return "itinerarys/create";
        }
        uiModel.asMap().clear();
        itinerary.persist();
        return "redirect:/itinerarys/" + encodeUrlPathSegment(itinerary.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String ItineraryController.createForm(Model uiModel) {
        populateEditForm(uiModel, new Itinerary());
        return "itinerarys/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String ItineraryController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("itinerary", Itinerary.findItinerary(id));
        uiModel.addAttribute("itemId", id);
        return "itinerarys/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String ItineraryController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("itinerarys", Itinerary.findItineraryEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Itinerary.countItinerarys() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("itinerarys", Itinerary.findAllItinerarys(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "itinerarys/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String ItineraryController.update(@Valid Itinerary itinerary, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, itinerary);
            return "itinerarys/update";
        }
        uiModel.asMap().clear();
        itinerary.merge();
        return "redirect:/itinerarys/" + encodeUrlPathSegment(itinerary.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String ItineraryController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Itinerary.findItinerary(id));
        return "itinerarys/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String ItineraryController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Itinerary itinerary = Itinerary.findItinerary(id);
        itinerary.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/itinerarys";
    }
    
    void ItineraryController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("itinerary_created_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
    
    void ItineraryController.populateEditForm(Model uiModel, Itinerary itinerary) {
        uiModel.addAttribute("itinerary", itinerary);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("flights", Flight.findAllFlights());
    }
    
    String ItineraryController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
