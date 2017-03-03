package com.amazonrec;

import java.io.*;



/**
 * Created by Prajakta Dhanawade
 */


class FileOperationInter {
    private String fileName;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;

    FileOperationInter(String fileNameIn, String operation) throws FileNotFoundException {
        fileName = fileNameIn;
        try {
            switch (operation) {
                case "R":
                    this.bufferedReader = new BufferedReader(new FileReader(fileName));
                    break;

                case "W":
                    this.bufferedWriter = new BufferedWriter(new FileWriter(fileName));
                    break;
            }
        } catch (FileNotFoundException e) {
            System.err.println("No such file found.");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error in file operation.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    String readLineFromFile(){
        String output = null;
        try{
            output = bufferedReader.readLine();
        } catch (IOException e) {
            System.err.println("Error in file reading.");
            e.printStackTrace();
            System.exit(1);
        }
        return output;
    }

    void writeLineToFile(String output){
        try {
            bufferedWriter.write(output);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeBufferedReader() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeBufferedWriter() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 
    @Override
    public String toString() {
        return "File Name=" + fileName;
    }
}
