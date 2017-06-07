import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Main {
	
	public static void main(String[] args){
		
		ArrayList<String> trigramListOfEnglishLanguage = new ArrayList<String>();
		ArrayList<String> trigramListOfFinnishLanguage = new ArrayList<String>();
		ArrayList<String> trigramListOfTurkishLanguage = new ArrayList<String>();
		ArrayList<String> bigramListOfEnglishLanguage = new ArrayList<String>();
		ArrayList<String> bigramListOfFinnishLanguage = new ArrayList<String>();
		ArrayList<String> bigramListOfTurkishLanguage = new ArrayList<String>();
		
		/*String folderOfEnglishDataset = "datasets/english/";
		String folderOfFinnishDataset = "datasets/finnish/";
		String folderOfTurkishDataset = "datasets/turkish/";
		
		ArrayList<String> listOfLineOfEnglishFiles = readAllFilesFromPath(folderOfEnglishDataset);
		ArrayList<String> listOfLineOfFinnishFiles = readAllFilesFromPath(folderOfFinnishDataset);
		ArrayList<String> listOfLineOfTurkishFiles = readAllFilesFromPath(folderOfTurkishDataset);
		
		String englishWords = separatePunctuationMarks(listOfLineOfEnglishFiles, "english");
		String finnishWrods = separatePunctuationMarks(listOfLineOfFinnishFiles, "finnish");
		String turkishWords = separatePunctuationMarks(listOfLineOfTurkishFiles, "turkish");
		
		createCharacterBasedNGramLanguageModel(englishWords, trigramListOfEnglishLanguage, bigramListOfEnglishLanguage);
		createCharacterBasedNGramLanguageModel(finnishWrods, trigramListOfFinnishLanguage, bigramListOfFinnishLanguage);
		createCharacterBasedNGramLanguageModel(turkishWords, trigramListOfTurkishLanguage, bigramListOfTurkishLanguage);
		
		
		writeListToFile("trainedDataSets/trigramListOfEnglishLanguage", trigramListOfEnglishLanguage);
		writeListToFile("trainedDataSets/trigramListOfFinnishLanguage", trigramListOfFinnishLanguage);
		writeListToFile("trainedDataSets/trigramListOfTurkishLanguage", trigramListOfTurkishLanguage);
		writeListToFile("trainedDataSets/bigramListOfEnglishLanguage", bigramListOfEnglishLanguage);
		writeListToFile("trainedDataSets/bigramListOfFinnishLanguage", bigramListOfFinnishLanguage);
		writeListToFile("trainedDataSets/bigramListOfTurkishLanguage", bigramListOfTurkishLanguage);
		*/
		
		readLineByLineTextFromFile("trainedDataSets/trigramListOfEnglishLanguage", trigramListOfEnglishLanguage);
		readLineByLineTextFromFile("trainedDataSets/trigramListOfFinnishLanguage", trigramListOfFinnishLanguage);
		readLineByLineTextFromFile("trainedDataSets/trigramListOfTurkishLanguage", trigramListOfTurkishLanguage);
		readLineByLineTextFromFile("trainedDataSets/bigramListOfEnglishLanguage", bigramListOfEnglishLanguage);
		readLineByLineTextFromFile("trainedDataSets/bigramListOfFinnishLanguage", bigramListOfFinnishLanguage);
		readLineByLineTextFromFile("trainedDataSets/bigramListOfTurkishLanguage", bigramListOfTurkishLanguage);
		
		String inputFileName = "input.txt";
		String outputFileName = "output.txt";
		
		ArrayList<String> listOfLineOfResult = detectLanguageOfInput(inputFileName, trigramListOfEnglishLanguage, trigramListOfFinnishLanguage, trigramListOfTurkishLanguage, bigramListOfEnglishLanguage, bigramListOfFinnishLanguage, bigramListOfTurkishLanguage);
		
		writeListToFile(outputFileName, listOfLineOfResult);

	}
	
	public static ArrayList<String> readAllFilesFromPath(String folderOfFiles){
		
		ArrayList<String> listOfLine = new ArrayList<String>();
		
		File folder = new File(folderOfFiles);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles)		//read all file under given folder
			if(file.isFile())
				readLineByLineTextFromFile(file.toString(), listOfLine);
		
		return listOfLine;
	}
	
	public static void readLineByLineTextFromFile(String inputFileName, ArrayList<String> listOfLine){
		
		String line;
		FileReader reader = null;
		BufferedReader bufferedReader = null;
		
		try{
			reader = new FileReader(inputFileName);
            bufferedReader = new BufferedReader(reader);
 
            while ((line = bufferedReader.readLine()) != null) {	
            	
            	if(line.length() > 0) //add line to list if it is not empty
            		listOfLine.add(line);          		
            }			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			
			try {						//close reader and bufferedReader
				reader.close();
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static String separatePunctuationMarks(ArrayList<String> listOfLine, String language){
		
		StringBuilder sb = new StringBuilder();
		
		for(String line : listOfLine){
			
			line = line.trim();
			
			if(!line.isEmpty() && line.length()>0){
				
				line = line.replace("...",".").replace("\t|\n", " ").replace("'", "");		//replace tab or new line character
				line = line.replaceAll("[^a-zA-Z| |Å|å|Ä|ä|Ö|ö|Ç|ç|Ğ|ğ|ı|İ|Ö|ö|Ş|ş|Ü|ü]", " ").toLowerCase();	

				if( language.equals("english") || language.equals("finnish") ){
					line = line.replace('ı', 'i');
				}
				
				line = line.replaceAll("\\s+", " ").trim();
				
				sb.append(line+" ");
			}
		}
		
		return sb.toString();
	}
	
	public static void createCharacterBasedNGramLanguageModel(String data, ArrayList<String> trigramList, ArrayList<String> bigramList){
			
		String[] arrayOfLetter = data.split("");
		
		for(int i=0; i<arrayOfLetter.length-2; i++){
			
			bigramList.add(arrayOfLetter[i] + arrayOfLetter[i+1]);
			trigramList.add(arrayOfLetter[i] + arrayOfLetter[i+1] + arrayOfLetter[i+2]);
		}
			
	}
		
	public static double calculateProbabilityOfGivenInput(String inputData, String language, ArrayList<String> trigramList, ArrayList<String> bigramList){
		
		double totalProbability = 1.0;
		
		String[] letterArray = inputData.split("");
		
		double probability;
		
		String C1C2, C1C2C3;
		
		int countOfC1C2, countOfC1C2C3;
		
		Set<String> uniqueBigramList = new HashSet<String>(bigramList);
		
		for(int i=2; i<letterArray.length; i++){

			C1C2 = letterArray[i-2] + letterArray[i-1];
			
			C1C2C3 = letterArray[i-2] + letterArray[i-1] + letterArray[i];
			
			countOfC1C2 = Collections.frequency(bigramList, C1C2);
			
			countOfC1C2C3 = Collections.frequency(trigramList, C1C2C3);

			if(countOfC1C2C3 == 0){
				
				countOfC1C2C3=1;
				
				countOfC1C2 += uniqueBigramList.size();
			}

			probability = (double)countOfC1C2C3 / (double)countOfC1C2;
			
			totalProbability *= probability;
		}

		return totalProbability;
	}
	
	public static ArrayList<String> detectLanguageOfInput(String inputFileName, ArrayList<String> trigramListOfEnglishLanguage, ArrayList<String> trigramListOfFinnishLanguage, ArrayList<String> trigramListOfTurkishLanguage, ArrayList<String> bigramListOfEnglishLanguage, ArrayList<String> bigramListOfFinnishLanguage, ArrayList<String> bigramListOfTurkishLanguage){
		
		ArrayList<String> listOfLineOfResult = new ArrayList<String>();
		
		ArrayList<String> listOfLineOfInputFile = new ArrayList<String>();
		
		readLineByLineTextFromFile(inputFileName, listOfLineOfInputFile);
		
		String inputData = separatePunctuationMarks(listOfLineOfInputFile, "undefined");
		
		double probabilityOfEnglish = calculateProbabilityOfGivenInput(inputData, "english", trigramListOfEnglishLanguage, bigramListOfEnglishLanguage);
		double probabilityOfFinnish = calculateProbabilityOfGivenInput(inputData, "finnish", trigramListOfFinnishLanguage, bigramListOfFinnishLanguage);
		double probabilityOfTurkish = calculateProbabilityOfGivenInput(inputData, "turkish", trigramListOfTurkishLanguage, bigramListOfTurkishLanguage);
		
		double sumOfProbability = probabilityOfEnglish + probabilityOfFinnish + probabilityOfTurkish;
		
		double max = probabilityOfEnglish;
		String detectedLanguage = "English";
		
		if(probabilityOfFinnish > max){
			max = probabilityOfFinnish;
			detectedLanguage = "Finnish";
		}
		if(probabilityOfTurkish > max){
			max = probabilityOfTurkish;
			detectedLanguage = "Turkish";
		}
			
		listOfLineOfResult.add("Detected language of input is " + detectedLanguage + ".");
		
		try{
			listOfLineOfResult.add("The probability that the language of this input is in English is % " + new BigDecimal(((probabilityOfEnglish*100)/sumOfProbability)).toPlainString());
		}
		catch(NumberFormatException e){
			listOfLineOfResult.add("The probability that the language of this input is in English is % 0");
		}
		
		try{
			listOfLineOfResult.add("The probability that the language of this input is in Finnish is % " + new BigDecimal(((probabilityOfFinnish*100)/sumOfProbability)).toPlainString());
		}
		catch(NumberFormatException e){
			listOfLineOfResult.add("The probability that the language of this input is in Finnish is % 0");
		}
		
		try{
			listOfLineOfResult.add("The probability that the language of this input is in Turkish is % 0" + new BigDecimal(((probabilityOfTurkish*100)/sumOfProbability)).toPlainString());
		}
		catch(NumberFormatException e){
			listOfLineOfResult.add("The probability that the language of this input is in Turkish is % 0");
		}
		
		return listOfLineOfResult;
	}
	
	public static void writeListToFile(String outputFileName, ArrayList<String> listOfLineOfResult){
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			
			fw = new FileWriter(outputFileName);
			bw = new BufferedWriter(fw);
			
			for (String line : listOfLineOfResult) {
				bw.write(line);
				bw.newLine();
			}

		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			
			try {						//close reader and bufferedReader
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
