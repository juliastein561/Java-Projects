import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.Math;
import java.util.LinkedList;
import java.util.ListIterator;
/*
 Author: Julia Stein
 Date: 3/4/24
 Description: This is the main submission file for CSC 2300 Project 1.
              This project involves ranked choice voting, where if no
              candidate receives over 50% of the votes, the votes which
              went to the loser initially now go to the second choice
              on the ballot, all the way to the total number candidate
              choices after removing the losers ballot.
 */
public class ElectionResults {

    // the main method works as follows:
    // - provided code (leave this code as is):
    //   -- prompts user for file name containing ballot data
    //   -- reads data into array (one array item per line in file)
    // - code you need to write:
    //   -- execute the Ranked Choice Voting process as outlined
    //     in the project description document by calling the other
    //     methods that you will implement in this project
    public static void main(String[] args) {
        // Establish console Scanner for console input
        Scanner console = new Scanner(System.in);

        // Determine the file name containing the ballot data
        System.out.print("Ballots file: ");
        String fileName = console.nextLine();
        if (fileName.equals("")) {
            fileName = "/Users/julia.stein/Desktop/CSC 2300/ElectionResults/src/BobSueBillSally.txt";
            // String fileName = "/Users/julia.stein/Desktop/CSC 2300/ElectionResults/src/JayKayElle.txt";
            // String fileName = "/Users/julia.stein/Desktop/CSC 2300/ElectionResults/src/AliceBobCarl.txt";
        }
        // Read the file contents into an array.  Each array
        // entry corresponds to a line in the file.
        String[] fileContents = getFileContents(fileName);

        // ***********************************************
        // Your code below here: execute the RCV process,
        // ensuring to make use of the remaining methods
        // ***********************************************

        ArrayList<Ballot> ballotArr = convert(fileContents);
        HashMap<String, Integer> resultsMap = tallies(ballotArr);
        System.out.println(resultsMap);

        printCounts(resultsMap); // method 5
        Result resultAnalysis = analyze(resultsMap);
        while(resultAnalysis.isLoser()) {
            System.out.println(resultAnalysis);
            String loserName = resultAnalysis.getName();

            // remove candidate from ballots
            remove(loserName, ballotArr);
            resultsMap = tallies(ballotArr);

            resultAnalysis = analyze(resultsMap);
            System.out.println(resultAnalysis);
        }
        // 4 - analyze()
        Result secondAnalysis = analyze(resultsMap);
        System.out.println(secondAnalysis);

        // 7 - printPercentages();
        printPercentages(resultsMap);
    }

    // Create your methods below here

    public static int totalCandidates;

    // 1) convert()
    public static ArrayList<Ballot> convert(String[] fileContents){
        ArrayList<Ballot> allBallots = new ArrayList<>();
        for (String fileContent : fileContents) {
            Ballot newBallot = new Ballot();
            String[] choices = fileContent.split(",");
            for (String choice : choices) {
                newBallot.addCandidate(choice);
            }
            allBallots.add(newBallot);
        }
        System.out.println(allBallots);
        return allBallots;
    }

    // 2) tallies()
    public static HashMap<String, Integer> tallies(ArrayList<Ballot> ballots){
        HashMap<String, Integer> resultsMap = new HashMap<>();
        for (Ballot currentBallot : ballots) {
            String ballotCandidates = currentBallot.getCurrentChoice();
            String[] candidatesArr = ballotCandidates.split(",");
            if (candidatesArr.length > totalCandidates){
                totalCandidates = candidatesArr.length;
            }
            for (String candidateKey : candidatesArr) {
                boolean vote = resultsMap.containsKey(candidateKey);
                if (!vote) {
                    resultsMap.put(candidateKey, 1);
                } else {
                    resultsMap.put(candidateKey, resultsMap.get(candidateKey) + 1);
                }
            }
        }
        return resultsMap;
    }

    // 3) countTotalVotes()
    public static int countTotalVotes(HashMap<String, Integer> votes){
        int totalCount = 0;
        for(String key : votes.keySet()) {
            totalCount += votes.get(key);
        }
        //System.out.println(totalCount);
        return totalCount;
    }

    // 4) analyze()
    public static Result analyze(HashMap<String, Integer> votes){
        int highestVoteCount = 0;
        int highestVoteKey = 0;
        String highestVoteCandidate = "";
        HashMap<String, Integer> firstRankMap = new HashMap<>();
        HashMap<String, Integer> loserTrackerMap = new HashMap<>();

        // build a map of all candidates to track their counts for loser check
        for(String candidateKey : votes.keySet()) {
            if (!loserTrackerMap.containsKey(candidateKey)) {
                loserTrackerMap.put(candidateKey, 0);
            }
        }

        // get high first rank count and candidate name
        for(String currentCandidate : votes.keySet()) {
            int currentVoteCount = votes.get(currentCandidate);
            if (currentVoteCount > highestVoteKey) {
                highestVoteCount = currentVoteCount;
                highestVoteCandidate = currentCandidate;
            }
            firstRankMap.put(currentCandidate, highestVoteCount);
//            loserTrackerMap.put(currentCandidate, loserTrackerMap.get(currentCandidate) + 1);
        }
        // get total vote count of all first rank candidates
        int totalFirstRankVoteCount = countTotalVotes(firstRankMap);

        System.out.println(" "); // formatting for output
        Result electionResult = new Result();
        for(String candidateKey : firstRankMap.keySet()) {
            double minimumVotes = Math.ceil((float) totalFirstRankVoteCount / 2);
            if (firstRankMap.get(candidateKey) >= minimumVotes) {
                electionResult.setName(candidateKey);
                electionResult.setWin(true);
                break;
            } else {
                electionResult.setName(candidateKey);
            }
        }
        // returning the lowest loser

        if (electionResult.isLoser()) {
//            double lowestCount = Double.POSITIVE_INFINITY;
//            String lowestCountCandidate = "";
//            for(String loser: loserTrackerMap.keySet()){
//                if (loserTrackerMap.get(loser) < lowestCount){
//                    lowestCount = loserTrackerMap.get(loser);
//                    lowestCountCandidate = loser;
//                }
//            }
//            electionResult.setName(lowestCountCandidate);
            System.out.println("Loser Below:");
//
        }
        return electionResult;
    }

    // 5) printCounts()
    public static void printCounts(HashMap<String, Integer> votes){
        System.out.println(" ");
        System.out.println("Print Counts Below:");
        for (String currentCandidate: votes.keySet()){
            System.out.println(currentCandidate + ": " + votes.get(currentCandidate));
        }
    }

    // 6) remove()
    public static void remove(String candidateName, ArrayList<Ballot> ballots){
        int numOfBallots = ballots.size();
        for (int i = numOfBallots - 1; i >= 0; i--) {
           if (ballots.get(i).getCurrentChoice().equals(candidateName)) {
               // REMOVING
               // System.out.println("Removing candidate " + candidateName + " from: \n" + ballots.get(i));
               ballots.get(i).removeCandidate(candidateName);
               if (ballots.get(i).isExhausted()) {
                   ballots.remove(i);
               }
           }
        }
    }

    // 7) printPercentages()
    public static void printPercentages(HashMap<String, Integer> votes){
        System.out.println(" ");
        System.out.println("Vote Percentages: ");
        for (String vote : votes.keySet()){
            double candidatePercentage = (float)votes.get(vote)/countTotalVotes(votes)*100;
            System.out.printf("%.2f%s", candidatePercentage, "% ");
            System.out.print(vote + "\n");
        }
    }

    // DO NOT edit the methods below. These are provided to help you get started.
    public static String[] getFileContents(String fileName) {
        // first pass: determine number of lines in the file
        Scanner file = getFileScanner(fileName);
        int numLines = 0;
        while (file.hasNextLine()) {
            file.nextLine();
            numLines++;
        }

        // create array to hold the number of lines counted
        String[] contents = new String[numLines];

        // second pass: read each line into array
        file = getFileScanner(fileName);
        for (int i = 0; i < numLines; i++) {
            contents[i] = file.nextLine();
        }
        return contents;
    }

    public static Scanner getFileScanner(String fileName) {
        try {
            FileInputStream textFileStream = new FileInputStream(fileName);
            Scanner inputFile = new Scanner(textFileStream);
            return inputFile;
        }
        catch (IOException ex) {
            System.out.println("Warning: could not open " + fileName);
            return null;
        }
    }
}