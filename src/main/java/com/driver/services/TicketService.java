package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    TrainService trainService;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        List<Integer> passengerIds = bookTicketEntryDto.getPassengerIds();
        List<Passenger> passengerList = new ArrayList<>();
        for(Integer passengerId : passengerIds){
            Optional<Passenger> optionalPassenger = passengerRepository.findById(passengerId);
            if(!optionalPassenger.isPresent())
                throw new Exception("passenger doesn't exists");
            passengerList.add(optionalPassenger.get());
        }
       // System.out.println("one");

        Optional<Passenger> optionalPassenger = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId());
        if(!optionalPassenger.isPresent())
            throw new Exception("passenger doesn't exists");
        Passenger bookingPerson = optionalPassenger.get();
        // passengerList.add(bookingPerson);
       // System.out.println("two");

        Optional<Train> optionalTrain = trainRepository.findById(bookTicketEntryDto.getTrainId());
        if(!optionalTrain.isPresent())
            throw new Exception("wrong train ID");

       // System.out.println("three");
        Train train = optionalTrain.get();
        Station from =  bookTicketEntryDto.getFromStation();
        Station to =    bookTicketEntryDto.getToStation();


        int availableSeats = trainService.calculateAvailableSeats(
                new SeatAvailabilityEntryDto(train.getTrainId(),from,to));

        if(availableSeats < bookTicketEntryDto.getNoOfSeats())
            throw new Exception("Less tickets are available");
        //System.out.println("four");

        String route = train.getRoute();
        List<String> stations = Arrays.asList(route.split(","));

        //System.out.println(stations);
        int indexOfFrom = stations.indexOf(from.toString());
        int indexOfTo = stations.indexOf(to.toString());
        //System.out.println(indexOfFrom+" "+indexOfTo);
        if(indexOfFrom == -1 || indexOfTo == -1 || indexOfFrom > indexOfTo)
            throw new Exception("Invalid stations");
        System.out.println("five");
        Ticket ticket = new Ticket();
        ticket.setPassengersList(passengerList);
       // ticket.getPassengersList().add(bookingPerson);
        ticket.setTrain(train);
        ticket.setFromStation(from);
        ticket.setToStation(to);
        ticket.setTotalFare(bookTicketEntryDto.getNoOfSeats() * (300 * (indexOfTo - indexOfFrom)));

        train.getBookedTickets().add(ticket);

        Train savedTrain = trainRepository.save(train);
        System.out.println(ticket.toString());

       // Ticket savedTicket = ticketRepository.save(ticket);


       // return savedTicket.getTicketId();
//        bookingPerson.getBookedTickets().add(ticket);
//      passengerRepository.save(bookingPerson);
////        train.getBookedTickets().add(ticket);
//
//        Train savedTrain = trainRepository.save(train);

        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db
        int lastTicketIndex = savedTrain.getBookedTickets().size()-1;
        Ticket ticket1 = savedTrain.getBookedTickets().get(lastTicketIndex);
        for (Passenger passenger : passengerList){
            passenger.getBookedTickets().add(ticket1);
            passengerRepository.save(passenger);
        }
        bookingPerson.getBookedTickets().add(ticket1);
        passengerRepository.save(bookingPerson);
       return ticket1.getTicketId();
       // return ticket.getTicketId();
    }
}
