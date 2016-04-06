package com.jpassion.myproject.mydomain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.TypedQuery;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findItinerarysByUsernameEquals", "findItinerarysByFlights", "findItinerarysByReturnflights" })
public class Itinerary {

    /** departing flights
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Flight> flights = new HashSet<Flight>();

    /** if it's business class itinerary
     */
    private Boolean business;

    /** Returns a string of the ids of departing flights
     * @return a string of the ids of departing flights
     */
    public String getDepartingIds() {
        List<String> ids = new ArrayList<String>();
        for (Flight flight : this.getFlights()) {
            ids.add(flight.getId().toString());
        }
        return String.join(",", ids);
    }

    /**
     * Returns a string of the ids of departing flights formated to show
     * @return a string of the ids of departing flights formated to show
     */
    public String getDepartingflightids() {
        StringBuilder builder = new StringBuilder();
        List<String> ids = new ArrayList<String>();
        for (Flight flight : this.getFlights()) {
            String desc = String.format("%s %s : %s - %s : %s", flight.getAirlinecode(), flight.getFlightnumber(), flight.getDeparturelocation(), flight.getArrivallocation(), flight.getDepartureday());
            ids.add(desc);
        }
        return String.join("\r\n", ids);
    }

    /** Returns a string of the ids of returning flights
     * @return a string of the ids of returning flights
     */
    public String getReturnIds() {
        List<String> ids = new ArrayList<String>();
        for (Flight flight : this.getReturnflights()) {
            ids.add(flight.getId().toString());
        }
        return String.join(",", ids);
    }


    /**
     * Returns a string of the ids of returning flights formated to show
     * @return a string of the ids of returning flights formated to show
     */
    public String getReturnflightids() {
        StringBuilder builder = new StringBuilder();
        List<String> ids = new ArrayList<String>();
        for (Flight flight : this.getReturnflights()) {
            String desc = String.format("%s %s : %s - %s : %s", flight.getAirlinecode(), flight.getFlightnumber(), flight.getDeparturelocation(), flight.getArrivallocation(), flight.getDepartureday());
            ids.add(desc);
        }
        return String.join("\r\n", ids);
    }
    
    /**
     * Returns total price of the whole itinerary
     * @return price
     */
    public float getTotalPrice() {
        float total = 0;
        for (Flight flight : this.getFlights()) {
            if (this.business) total += flight.getCostbusiness(); else total += flight.getCosteconomy();
        }
        for (Flight flight : this.getReturnflights()) {
            if (this.business) total += flight.getCostbusiness(); else total += flight.getCosteconomy();
        }
        return total;
    }

    /**
     * Returns date which is an hour before / or after the current
     * @param current
     * @param minus
     * @return date which is an hour before / or after the current
     */
    static Date addHour(Date current, boolean minus) {
        long time = current.getTime();
        long hour = (long) (60 * 60 * 1000);
        if (minus) return new Date(time - hour); else return new Date(time + hour);
    }

    /**
     * Returns date which is 2 minutes before the current
     * @param current
     * @return date which is 2 minutes before the current
     */
    static Date minus2Minues(Date current) {
        long time = current.getTime();
        long m2 = (long) (2 * 60 * 1000);
        return new Date(time - m2);
    }

    /**
     * Search flights to get to the final destination recursively
     * @param em
     * @param foundRoutes
     * @param currentRoute
     * @param currentlocation
     * @param destinationlocation
     * @param currentDay
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @param business
     */
    static void searchRecursively(EntityManager em, ArrayList<ArrayList<Flight>> foundRoutes, List<Flight> currentRoute, String currentlocation, String destinationlocation, Date currentDay, Integer numberOfPassengers, Integer maxNumberOfStops, boolean business) {
        if (currentRoute.size() >= maxNumberOfStops) return;
        TypedQuery<Flight> q = em.createQuery("SELECT o FROM Flight AS o WHERE o.departurelocation = :departurelocation AND o.departureday >= :currentDay", Flight.class);
        q.setParameter("departurelocation", currentlocation);
        q.setParameter("currentDay", currentDay);
        List<Flight> result = q.getResultList();
        for (Flight flight : result) {
            if (business) {
                if (flight.getCancelled() || flight.getNumbersOfUnBookedBusinessSeats() < numberOfPassengers) continue;
            } else {
                if (flight.getCancelled() || flight.getNumberOfEconomySeats() < numberOfPassengers) continue;
            }
            ArrayList<Flight> flights = new ArrayList<Flight>(currentRoute);
            flights.add(flight);
            //found a route to the destination
            if (flight.getArrivallocation().equals(destinationlocation)) {
                foundRoutes.add(flights);
            } else {
                //find continuing routes
                Date nextFlightableDate = addHour(flight.getArrivalday(), false);
                searchRecursively(em, foundRoutes, flights, flight.getArrivallocation(), destinationlocation, nextFlightableDate, numberOfPassengers, maxNumberOfStops, business);
            }
        }
    }

    /**
     * Returns itineraries from departure location to arrival location
     * @param departurelocation
     * @param arrivallocation
     * @param departureday
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @param business
     * @return itineraries from departure location to arrival location
     */
    static List<Itinerary> search(String departurelocation, String arrivallocation, Date departureday, Integer numberOfPassengers, Integer maxNumberOfStops, boolean business) {
        EntityManager em = Flight.entityManager();
        ArrayList<ArrayList<Flight>> foundRoutes = new ArrayList<ArrayList<Flight>>();
        TypedQuery<Flight> q = em.createQuery("SELECT o FROM Flight AS o WHERE o.departurelocation = :departurelocation AND o.departureday >= :departuredayFrom AND o.departureday <= :departuredayTo", Flight.class);
        q.setParameter("departurelocation", departurelocation);
        q.setParameter("departuredayFrom", addHour(departureday, true));
        q.setParameter("departuredayTo", addHour(departureday, false));
        List<Flight> result = q.getResultList();
        for (Flight flight : result) {
            if (business) {
                if (flight.getCancelled() || flight.getNumbersOfUnBookedBusinessSeats() < numberOfPassengers) continue;
            } else {
                if (flight.getCancelled() || flight.getNumberOfEconomySeats() < numberOfPassengers) continue;
            }
            ArrayList<Flight> flights = new ArrayList<Flight>();
            flights.add(flight);
            //found a direct flight
            if (flight.getArrivallocation().equals(arrivallocation)) {
                foundRoutes.add(flights);
            } else {
                //find continuing routes
                Date nextFlightableDate = addHour(flight.getArrivalday(), false);
                searchRecursively(em, foundRoutes, flights, flight.getArrivallocation(), arrivallocation, nextFlightableDate, numberOfPassengers, maxNumberOfStops, business);
            }
        }
        //   System.out.println("Count : " + foundRoutes.get(0).size());
        List<Itinerary> ret = new ArrayList<Itinerary>();
        for (ArrayList<Flight> route : foundRoutes) {
            Itinerary newItinerary = new Itinerary();
            newItinerary.setNumberofpassengers(numberOfPassengers);
            newItinerary.setBusiness(business);
            for (Flight flight : route) {
                newItinerary.getFlights().add(flight);
            }
            ret.add(newItinerary);
        }
        return ret;
    }

    /**
     * Returns one way itineraries from departure location to arrival location
     * @param departurelocation
     * @param arrivallocation
     * @param departureday
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @return itineraries from departure location to arrival location
     */
    public static List<Itinerary> search(String departurelocation, String arrivallocation, Date departureday, Integer numberOfPassengers, Integer maxNumberOfStops) {
        if (departurelocation == null || departurelocation.length() == 0) throw new IllegalArgumentException("The departurelocation argument is required");
        List<Itinerary> itineraries = search(departurelocation, arrivallocation, departureday, numberOfPassengers, maxNumberOfStops, true);
        itineraries.addAll(search(departurelocation, arrivallocation, departureday, numberOfPassengers, maxNumberOfStops, false));
        return itineraries;
    }

    /**
     * Returns two way itineraries from departure location to arrival location
     * @param departurelocation
     * @param arrivallocation
     * @param departureday
     * @param arrivalday
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @return two way itineraries from departure location to arrival location
     */
    public static List<Itinerary> search(String departurelocation, String arrivallocation, Date departureday, Date arrivalday, Integer numberOfPassengers, Integer maxNumberOfStops) {
        List<Itinerary> deparingItineraries = search(departurelocation, arrivallocation, departureday, numberOfPassengers, maxNumberOfStops, true);
        deparingItineraries.addAll(search(departurelocation, arrivallocation, departureday, numberOfPassengers, maxNumberOfStops, false));
        List<Itinerary> returnItineraries = searchReturn(arrivallocation, departurelocation, arrivalday, numberOfPassengers, maxNumberOfStops, true);
        returnItineraries.addAll(searchReturn(arrivallocation, departurelocation, arrivalday, numberOfPassengers, maxNumberOfStops, false));
      
        List<Itinerary> itineraries = new ArrayList<Itinerary>();
        //merge both way
        for (Itinerary departing : deparingItineraries) {
            for (Itinerary returning : returnItineraries) {
                //System.out.println(returning.business);
                if (departing.business != returning.business) continue;
                Itinerary itinerary = new Itinerary();
                itinerary.numberofpassengers = departing.numberofpassengers;
                itinerary.business = departing.business;
                itinerary.flights = new HashSet<Flight>(departing.flights);
                itinerary.returnflights = new HashSet<Flight>(returning.flights);
                itineraries.add(itinerary);
            }
        }
        return itineraries;
    }

    /**
     * Search return flights recursively
     * @param em
     * @param foundRoutes
     * @param currentRoute
     * @param currentlocation
     * @param departurelocation
     * @param currentDay
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @param business
     */
    static void searchReturnRecursively(EntityManager em, ArrayList<ArrayList<Flight>> foundRoutes, List<Flight> currentRoute, String currentlocation, String departurelocation, Date currentDay, Integer numberOfPassengers, Integer maxNumberOfStops, boolean business) {
        if (currentRoute.size() >= maxNumberOfStops) return;
        TypedQuery<Flight> q = em.createQuery("SELECT o FROM Flight AS o WHERE o.arrivallocation = :arrivallocation AND o.arrivalday <= :currentDay", Flight.class);
        q.setParameter("arrivallocation", currentlocation);
        q.setParameter("currentDay", currentDay);
        List<Flight> result = q.getResultList();
        //System.out.println(currentlocation);
        //System.out.println(currentDay);
        // System.out.println("Count : " + result.size());
        for (Flight flight : result) {
            if (business) {
                if (flight.getCancelled() || flight.getNumbersOfUnBookedBusinessSeats() < numberOfPassengers) continue;
            } else {
                if (flight.getCancelled() || flight.getNumberOfEconomySeats() < numberOfPassengers) continue;
            }
            ArrayList<Flight> flights = new ArrayList<Flight>(currentRoute);
            flights.add(flight);
            //found a route to the destination
            if (flight.getDeparturelocation().equals(departurelocation)) {
                foundRoutes.add(flights);
            } else {
                //find continuing routes
                Date nextFlightableDate = addHour(flight.getDepartureday(), true);
                searchRecursively(em, foundRoutes, flights, flight.getDeparturelocation(), departurelocation, nextFlightableDate, numberOfPassengers, maxNumberOfStops, business);
            }
        }
    }

    /**
     * Returns return itineraries from arrival location to departure location
     * @param departurelocation
     * @param arrivallocation
     * @param arrivalDay
     * @param numberOfPassengers
     * @param maxNumberOfStops
     * @param business
     * @return return itineraries from arrival location to departure location
     */
    static List<Itinerary> searchReturn(String departurelocation, String arrivallocation, Date arrivalDay, Integer numberOfPassengers, Integer maxNumberOfStops, boolean business) {
        EntityManager em = Flight.entityManager();
        ArrayList<ArrayList<Flight>> foundRoutes = new ArrayList<ArrayList<Flight>>();
        TypedQuery<Flight> q = em.createQuery("SELECT o FROM Flight AS o WHERE o.arrivallocation = :arrivallocation AND o.arrivalday >= :arrivaldayFrom AND o.arrivalday <= :arrivaldayTo", Flight.class);
        q.setParameter("arrivallocation", arrivallocation);
        q.setParameter("arrivaldayFrom", addHour(arrivalDay, true));
        q.setParameter("arrivaldayTo", addHour(arrivalDay, false));
        List<Flight> result = q.getResultList();
        for (Flight flight : result) {
            if (business) {
                if (flight.getCancelled() || flight.getNumbersOfUnBookedBusinessSeats() < numberOfPassengers) continue;
            } else {
                if (flight.getCancelled() || flight.getNumberOfEconomySeats() < numberOfPassengers) continue;
            }
            ArrayList<Flight> flights = new ArrayList<Flight>();
            flights.add(flight);
            //found a direct flight
            if (flight.getDeparturelocation().equals(departurelocation)) {
                foundRoutes.add(flights);
            } else {
                //find continuing routes
                Date nextFlightableDate = addHour(flight.getDepartureday(), true);
                searchReturnRecursively(em, foundRoutes, flights, flight.getDeparturelocation(), departurelocation, nextFlightableDate, numberOfPassengers, maxNumberOfStops, business);
            }
        }
        //System.out.println("Count : " + foundRoutes.size());
        List<Itinerary> ret = new ArrayList<Itinerary>();
        for (ArrayList<Flight> route : foundRoutes) {
            Itinerary newItinerary = new Itinerary();
            newItinerary.setNumberofpassengers(numberOfPassengers);
            newItinerary.setBusiness(business);
            for (int i = route.size() - 1; i >= 0; i--) {
                Flight flight = route.get(i);
                newItinerary.getFlights().add(flight);
            }
            ret.add(newItinerary);
        }
        return ret;
    }

    /**
     * Returns valid itineraries belonging to the user
     * @param username
     * @return valid itineraries belonging to the user
     */
    public static TypedQuery<Itinerary> findValidItinerarysByUsernameEquals(String username) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        EntityManager em = Itinerary.entityManager();
        TypedQuery<Itinerary> q = em.createQuery("SELECT o FROM Itinerary AS o WHERE o.username = :username AND (o.status = 'booked' OR o.created >= :m2)", Itinerary.class);
        q.setParameter("username", username);
        q.setParameter("m2", minus2Minues(new Date()));
        return q;
    }

    /**
     * created date
     */
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date created = new Date();

    /**
     * status : reserved, booked or cancelled
     */
    @NotNull
    @Value("reserved")
    private String status;

    /**
     * user's name who booked the itinerary
     */
    @NotNull
    private String username;

    /**
     * return flights
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Flight> returnflights = new HashSet<Flight>();

    /**
     * Returns sorted departing flights by those departing time
     * @return sorted departing flights
     */
    public List<Flight> getSortedDepartingFlights() {
        List<Flight> flights = new ArrayList<Flight>(this.getFlights());
        Collections.sort(flights);
        return flights;
    }

    /**
     * Returns sorted return flights by those departing time
     * @return sorted return flights
     */
    public List<Flight> getSortedReturnFlights() {
        List<Flight> flights = new ArrayList<Flight>(this.getReturnflights());
        Collections.sort(flights);
        return flights;
    }

    /**
     * Returns if the itinerary has return flight
     * @return if the itinerary has return flight
     */
    public boolean getHasReturnFlights(){
    	return this.getReturnflights().size() > 0;
    }
    
    /**
     * Number of passengers doing this itinerary
     */
    private int numberofpassengers;
}
