package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;


    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library

        Train train = new Train();

        List<Station> stationList = trainEntryDto.getStationRoute();
        int len = stationList.size();

        StringBuilder sb = new StringBuilder();
       for(int i = 0; i < len; i++){
           sb.append(stationList.get(i));
           if(i < len - 1) sb.append(",");
       }

        train.setRoute(sb.toString());
        train.setDepartureTime(trainEntryDto.getDepartureTime());
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        Train train1 = trainRepository.save(train);

        return train1.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

        Optional<Train> optionalTrain = trainRepository.findById(seatAvailabilityEntryDto.getTrainId());
        Train train = optionalTrain.get();
        List<Ticket> ticketList = train.getBookedTickets();


        int ans = train.getNoOfSeats();
//         ans += train.getNoOfSeats() - ticketList.size();
        System.out.println("total seats "+ans);
        Station fromStation = seatAvailabilityEntryDto.getFromStation();
        Station toStation = seatAvailabilityEntryDto.getToStation();
         for(Ticket ticket : ticketList){
             if(!(ticket.getToStation().compareTo(fromStation) <= 0)|| !(ticket.getFromStation().compareTo(toStation)>=0))
                 ans -= ticket.getPassengersList().size();
             System.out.println(ticket.getPassengersList());
         }


        return ans;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.

        Optional<Train> optionalTrain = trainRepository.findById(trainId);
        Train train = optionalTrain.get();
        String routeString = train.getRoute();
        List<String> routeList = Arrays.asList(routeString.split(","));
        if(!routeList.contains(station.toString()))
            throw new Exception("Train is not passing from this station");

        int count = 0;
        List<Ticket> bookedTickets = train.getBookedTickets();
        for(Ticket ticket : bookedTickets){
            if(ticket.getFromStation().equals(station))
                count++;
        }

        return count;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        Optional<Train> optionalTrain = trainRepository.findById(trainId);
        Train train = optionalTrain.get();
//        train.
        return 0;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        return null;
    }

}
