package com.amazonrec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prajakta Dhanawade
 */
public class AmazonRecommender {
    //instance variables
    private final int totalUser = 943, totalItems = 1682;
    private int[][] inputMatrix = new int[totalUser+1][totalItems+1];
    private int[][] outputMatrix = new int[totalUser+1][totalItems+1];

    private Map<Integer, List<Integer>> userWithSimilarTasteMap = new HashMap<>();
    private Map<Integer, HashMap<Integer,Double>> firstToSecondUserCoefficientMap = new HashMap<>();

    //reference variables of other classes
    private FileOperationInter fileProcessor = null;

    //constructor
    AmazonRecommender(FileOperationInter fileProcessorin){
        fileProcessor = fileProcessorin;
    }
    void initializeMatrix(){
        String input = null;
        while((input = fileProcessor.readLineFromFile()) != null){
            String[] tokens = input.trim().split("\\s+");
            int userIndex = Integer.parseInt(tokens[0]);
            int itemIndex = Integer.parseInt(tokens[1]);
            int rating = Integer.parseInt(tokens[2]);
            inputMatrix[userIndex][itemIndex] = rating;
        }
    }
    void findCorrelation(){
        //For Each User
        for (int firstUser = 1; firstUser < inputMatrix.length; firstUser++) {
            List<Integer> usersWithSimilarTasteList = new ArrayList<>();
            HashMap<Integer,Double> user2CoefficientMap = new HashMap<>();
            //For Each Other User,
            for (int secondUser = 1; secondUser < inputMatrix.length; secondUser ++) {
                // we are not considering same user of course. Skip it.
                if(firstUser == secondUser) continue;

                double weightCoefficient = 0.0;
                //Pass both User's item rating for all items
                weightCoefficient = getCoefficient(inputMatrix[firstUser], inputMatrix[secondUser]);
                //threshold value for weighCoefficient is above 0.20 to consider both users has same taste.
                if(weightCoefficient > 0.20){
                    usersWithSimilarTasteList.add(secondUser);
                }
                //Store map of Second User <-> Weight for FirstUSer
                user2CoefficientMap.put(secondUser, weightCoefficient);
            }
            //Store first user's data in map for later use.
            firstToSecondUserCoefficientMap.put(firstUser, user2CoefficientMap);
            userWithSimilarTasteMap.put(firstUser, usersWithSimilarTasteList);
        }
    }

    private double getCoefficient(int[] firstUserItemRatingList, int[] secondUserItemRatingList){
        double weightCoefficient = 0.0, firstUserMeanRating = 0.0, secondUserMeanRating = 0.0;
        for (int rating =1 ;rating< firstUserItemRatingList.length; rating++) {
            firstUserMeanRating += firstUserItemRatingList[rating];
            secondUserMeanRating += secondUserItemRatingList[rating];
        }
        firstUserMeanRating /= firstUserItemRatingList.length-1;
        secondUserMeanRating /= secondUserItemRatingList.length-1;
        /*Perason's correlation formula
                                  Numerator
            w(a,i) = Summation ( [x-Xmean] * [y-Ymean] )/
                     Sqr ( Summation( pow[x-Xmean]2) * Summation(pow[y-Ymean]2))
                            Denominator A                   Denominator B
         */
        double numerator = 0.0, denominatorA = 0.0, denominatorB = 0.0;
        for (int rating = 1; rating < firstUserItemRatingList.length; rating++) {
            numerator += (firstUserItemRatingList[rating] - firstUserMeanRating) * (secondUserItemRatingList[rating] - secondUserMeanRating);
            denominatorA += Math.pow(firstUserItemRatingList[rating] - firstUserMeanRating,2);
            denominatorB += Math.pow(secondUserItemRatingList[rating] - secondUserMeanRating ,2);
        }
        weightCoefficient = numerator / Math.sqrt(denominatorA*denominatorB);

        return  weightCoefficient;
    }

    /**
     *  This method helps to call to overloading recommend function
     */
    void recommend(){
        for (int userIndex = 1; userIndex <= totalUser ; userIndex ++) {
            recommend(userIndex, userWithSimilarTasteMap.get(userIndex));
        }
    }

    private void recommend(int firstUserIndex, List<Integer> usersWithSimilarTasteList){
        //look for values of item's rating which are zero.
        for (int itemIndex = 1; itemIndex< totalItems; itemIndex++) {
            if(inputMatrix[firstUserIndex][itemIndex] == 0){
                double numerator = 0.0, denominator = 0.0;
                for ( int secondUserIndex: usersWithSimilarTasteList) {
                    if(inputMatrix[secondUserIndex][itemIndex]==0) continue;
                    //get first User's <-> Second User coefficient value we calculated earlier.
                    numerator += inputMatrix[secondUserIndex][itemIndex] *  firstToSecondUserCoefficientMap.get(firstUserIndex).get(secondUserIndex);
                    //Get summation of its absolute value as denominator
                    denominator += Math.abs(firstToSecondUserCoefficientMap.get(firstUserIndex).get(secondUserIndex));
                }
                double recommendedRating = Math.round(numerator/denominator);
                if(recommendedRating < 1){
                    recommendedRating = 1;
                }else if(recommendedRating > 5){
                    recommendedRating = 5;
                }
                //store recommendedValue to the output matrix.
                outputMatrix[firstUserIndex][itemIndex] = (int) recommendedRating;
            }else{
                outputMatrix[firstUserIndex][itemIndex] = inputMatrix[firstUserIndex][itemIndex];
            }
        }
    }


    void printToFile(FileOperationInter outputFileProcessor){
        for (int userIndex = 1; userIndex < totalUser; userIndex++) {
            for (int itemIndex = 1;  itemIndex < totalItems;  itemIndex++) {
                String line = userIndex + " " + itemIndex + " " + outputMatrix[userIndex][itemIndex];
                outputFileProcessor.writeLineToFile(line);
            }
        }

    }

   
    void writeOutBuffer(FileOperationInter inputFileProcessor , FileOperationInter outputFileProcessor){
        assert inputFileProcessor != null;
        inputFileProcessor.closeBufferedReader();
        assert outputFileProcessor != null;
        outputFileProcessor.closeBufferedWriter();
    }

}
