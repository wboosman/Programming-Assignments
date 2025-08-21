import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class
 * @author 589273wb Wessel Boosman
 */

public class NaiveBayes {
    private String[] wordsOfInterest;
    private int amountSpamDocs;
    private int amountNoSpamDocs;
    private int totalDocs;
    private ArrayList<WordCounter> list;
    private double probSpam;
    private double probNoSpam;



    /**
     * This constructor create a WordCounter object for each focusword
     *
     * @param focusWords String array of focuswords. For each focusword, an WordCounter object is created
     */

    public NaiveBayes(String[] focusWords) {
        list = new ArrayList<WordCounter>();
        wordsOfInterest = focusWords;
        for (String s : wordsOfInterest) {
            WordCounter wc = new WordCounter(s);
            list.add(wc);
        }
    }

    /**
     * This method checks if the input document is classified and if so, which classification it has. It keeps track of the amount none-spam, spam and total documents.
     * Each created WordCounter object gets the document input and carries out the addsample method from the WordCounter class.
     * Initial probability of spam/no spam is calculated on the basis of the ratio of (spam/no-spam documents)/ (total documents)
     * @throws IllegalArgumentException when the input is not classified
     * @param document fdafdafda
     */
    public void addSample(String document) {
        String[] words = document.split(" ");

        if (!words[0].equals("0") && !words[0].equals("1")) {
            throw new IllegalArgumentException("Input is not classified");
        }
        if (words[0].equals("0")) {
            amountNoSpamDocs += 1;
        } else {
            amountSpamDocs += 1;
        }
        totalDocs = amountSpamDocs + amountNoSpamDocs;

        for (WordCounter wordCounter : list) {
            wordCounter.addSample(document);
        }

        probNoSpam = (double) amountNoSpamDocs / (double) totalDocs;
        probSpam = (double) amountSpamDocs / (double) totalDocs;
    }

    /**
     * This method is meant to classify unclassified documents. If the document contains any of the focuswords,
     * the probability for spam/ no spam gets updated. I.e. The old spam/ no spam score is multiplied with getConditionalSpam/ getConditionalNoSpam from the WordCounter class.
     *
     * @param unclassifiedDocument document input which starts with no 1 or 0.
     * @return true if the probability of no spam is smaller then spam, meaning probably spam. False for situation vice versa
     */
    public boolean classify(String unclassifiedDocument) {
        String[] unclassifiedWords = unclassifiedDocument.split(" ");
        probNoSpam = (double) amountNoSpamDocs / (double) totalDocs;
        probSpam = (double) amountSpamDocs / (double) totalDocs;

        for (int x = 0; x < wordsOfInterest.length; x++) {
            for (String unclassifiedWord : unclassifiedWords) {
                if (unclassifiedWord.equals(wordsOfInterest[x])) {
                    probSpam = probSpam * list.get(x).getConditionalSpam();
                    probNoSpam = probNoSpam * list.get(x).getConditionalNoSpam();
                }
            }
        }
        return probNoSpam < probSpam;
    }
    /**
     * This method trains the NaiveBayes object by the input trainingFile, every line of the file is considered as a document with classification.
     * Does the same as inputting each line from the file individually to the addSample method.
     * @param trainingFile a file where each line is considered a classified document and start therefor with a zero or one.
     * @throws IOException when the file is not found.
     */
    public void trainClassifier(File trainingFile) throws IOException {
        try (Scanner scan = new Scanner(new FileReader(trainingFile))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                System.out.println(line);
                addSample(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method classifies a whole file. Each line is considered a document and it is checked if it is probably spam or no spam.
     * The classification (1 or 0) is written to a new file.
     *
     * @param input input file with every line a document which needs to be classified.
     * @param output output file with classification for each line/document
     * @throws IOException if the input file is not found
     */
    public void classifyFile(File input, File output) throws IOException {

        long lengthInputFile;
        List<String> classifyList = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(input.toPath());
            lengthInputFile = Files.lines(input.toPath()).count();
            for (int u = 0; u < lengthInputFile; u++) {
                if (!classify(lines.get(u))) {
                    classifyList.add("0");
                } else {
                    classifyList.add("1");
                }
            }
            System.out.println(classifyList);
            Files.write(output.toPath(), classifyList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if the the classifier works properly. A test file of which the classification is known is treated as if it is not known.
     * The classification of the NaiveBayes object is compared to the real classification. The accuracy gets store in a matrix cm.
     * cm[0][0] = true positive
     * cm[0][1] = false negative
     * cm[1][0] = false positive
     * cm[1][1] = true negative
     * @param testdata is the data which is considered as unclassified, but in reality is already classified.
     * @return  ConfusionMatrix object, in this case an integer matrix
     * @throws IOException when the testdata is not found
     */
    public ConfusionMatrix computeAccuracy(File testdata) throws IOException {
        long lengthInputFile;
        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
        try (Scanner classifyScan = new Scanner(new FileReader(testdata))) {
            List<String> lines = Files.readAllLines(testdata.toPath());
            lengthInputFile = Files.lines(testdata.toPath()).count();
            for (int u = 0; u < lengthInputFile; u++) {
                if (lines.get(u).substring(0,1).equals("1"))
                {
                    if(!classify(lines.get(u).substring(1)))
                    {
                        confusionMatrix.incrementFalseNeg();
                    }
                    else{
                        confusionMatrix.incrementTruePos();
                    }
                }
                else if(lines.get(u).substring(0,1).equals("0")){
                    if(!classify(lines.get(u).substring(1)))
                    {
                        confusionMatrix.incrementTrueNeg();
                    }
                    else{
                        confusionMatrix.incrementFalsePos();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return confusionMatrix;
    }

}
