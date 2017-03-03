package com.amazonrec;

import java.io.FileNotFoundException;


/**
 * Created by Prajakta Dhanawade
 */
public class Controller {
    public static void main(String[] args) {
        String inputFile = "train_all_txt.txt";
        String outputfile = "output.txt";
        FileOperationInter inputFileProcessor = null;
        FileOperationInter outputFileProcessor = null;

        try {
            inputFileProcessor = new FileOperationInter(inputFile, "R");
            outputFileProcessor = new FileOperationInter(outputfile, "W");


            AmazonRecommender recommender = new AmazonRecommender(inputFileProcessor);
            recommender.initializeMatrix();
            recommender.findCorrelation();
            recommender.recommend();
            recommender.printToFile(outputFileProcessor);
            recommender.writeOutBuffer(inputFileProcessor, outputFileProcessor);

           
        } catch (FileNotFoundException e) {
            System.err.println("Error in reading the arguments.");
            e.printStackTrace();
            System.exit(1);
        }finally {
            assert inputFileProcessor != null;
            inputFileProcessor.closeBufferedReader();
            assert outputFileProcessor != null;
            outputFileProcessor.closeBufferedWriter();
        }
    }
}
