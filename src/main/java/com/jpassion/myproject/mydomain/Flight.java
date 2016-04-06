package com.jpassion.myproject.mydomain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import com.jpassion.myproject.validation.AirlineCodeValid;
import com.jpassion.myproject.validation.AirportCodeValid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.springframework.format.annotation.DateTimeFormat;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Flight implements Comparable<Flight> {

    /** a two-letter airline code
     */
    @NotNull
    @Value("BA")
    @Size(min = 2, max = 2)
    @AirlineCodeValid
    private String airlinecode;

    /** airline name
     */
    @NotNull
    private String airlinename;

    /** flight number
     */
    @NotNull
    @Size(max = 3)
    @Pattern(regexp = "[0-9]*")
    private String flightnumber;

    /** day the flight departs
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    @NotNull
    private Date departureday;

    /** a three-letter airport code
     */
    @NotNull
    @Value("LHR")
    @Size(min = 3, max = 3)
    @AirportCodeValid
    private String departurelocation;

    /** day the flight arrives
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    @NotNull
    private Date arrivalday;

    /** a three-letter airport code
     */
    @NotNull
    @Value("HND")
    @Size(min = 3, max = 3)
    @AirportCodeValid
    private String arrivallocation;

    /** price of a economy seat
     */
    @NotNull
    @Value("500")
    private Float costeconomy;

    /**  price of a business seat
     */
    @NotNull
    @Value("1000")
    private Float costbusiness;

    
    /**
     * Returns the flight which has the id given
     * @param id
     * @return the flight
     */
    public static Flight findById(Long id) {
        EntityManager em = Flight.entityManager();
        TypedQuery<Flight> q = em.createQuery("SELECT o FROM Flight AS o WHERE o.id = :id", Flight.class);
        q.setParameter("id", id);
        List<Flight> result = q.getResultList();
        if (result.size() >= 1) return result.get(0); else return null;
    }

    @Override
    public int compareTo(Flight another) {
        if (this.departureday.before(another.departureday)) return -1; else return 1;
    }

    /** departure time
     */
    @NotNull
    private String departuretime;

    /** arrival time
     */
    @NotNull
    private String arrivaltime;

    /**
     * Returns the number of economy seats
     * @return the number of economy seats
     */
    public int getNumberOfEconomySeats() {
        return 90;
    }

    /**
     * Returns the number of business seats
     * @return the number of business seats
     */
    public int getNumberOfBusinessSeats() {
        return 10;
    }

    /**
     * Return the number of empty economy seats
     * @return the number of empty economy seats
     */
    public int getNumbersOfUnBookedEconomySeats() {
        return this.getNumberOfEconomySeats() - findNumbersOfBooked(false);
    }

    /**
     * Return the number of empty business seats
     * @return the number of empty business seats
     */
    public int getNumbersOfUnBookedBusinessSeats() {
        return this.getNumberOfBusinessSeats() - findNumbersOfBooked(true);
    }

    /**
     * Return the total price of the unsold seats
     * @return the total price of the unsold seats
     */
    public float getTotalUnsoldCost() {
        return getNumbersOfUnBookedEconomySeats() * this.costeconomy + getNumbersOfUnBookedBusinessSeats() * this.costbusiness;
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
     * Returns itineraries which contains this flight as either departing flight or returning flight
     * @param business
     * @return list of itineraries
     */
    public List<Itinerary> findItinerys(boolean business) {
        List<Itinerary> ret = new ArrayList<Itinerary>();
        EntityManager em = Itinerary.entityManager();
        //departing
        {
            StringBuilder queryBuilder = new StringBuilder("SELECT o FROM Itinerary AS o WHERE o.status <> 'cancelled' AND (o.status = 'booked' OR o.created >= :m2) AND o.business = :business AND ");
            queryBuilder.append(" :flights_item").append(0).append(" MEMBER OF o.flights");
            TypedQuery<Itinerary> q = em.createQuery(queryBuilder.toString(), Itinerary.class);
            int flightsIndex = 0;
            q.setParameter("flights_item" + 0, this);
            q.setParameter("m2", minus2Minues(new Date()));
            q.setParameter("business", business);
            ret.addAll(q.getResultList());
        }
        //return
        {
            StringBuilder queryBuilder = new StringBuilder("SELECT o FROM Itinerary AS o WHERE o.status <> 'cancelled' AND (o.status = 'booked' OR o.created >= :m2) AND o.business = :business AND ");
            queryBuilder.append(" :returnflights_item").append(0).append(" MEMBER OF o.returnflights");
            TypedQuery<Itinerary> q = em.createQuery(queryBuilder.toString(), Itinerary.class);
            int flightsIndex = 0;
            q.setParameter("returnflights_item" + 0, this);
            q.setParameter("m2", minus2Minues(new Date()));
            q.setParameter("business", business);
            ret.addAll(q.getResultList());
        }
        return ret;
    }

    /**
     * Returns the number of the seats booked
     * @param business
     * @return the number of the seats booked
     */
    int findNumbersOfBooked(boolean business) {
        EntityManager em = Itinerary.entityManager();
        int num = 0;
        for (Itinerary itinerary : findItinerys(business)) {
            num += itinerary.getNumberofpassengers();
        }
        return num;
    }

    /** if the flight is cancelled
     */
    @Value("false")
    private Boolean cancelled;
}
