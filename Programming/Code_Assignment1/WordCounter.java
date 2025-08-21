/**
 * This class keeps track of the amount that a certain focusword is in a document and gives initial estimates for the probability if a document is apm on the basis of the focusword.
 * @author 589273wb Wessel Boosman
 */

public class WordCounter {
    private String wordOfInterest;
    private int amountSpamDocs;
    private int amountNoSpamDocs;
    private int totalSpamDocWords;
    private int totalNoSpamDocWords;
    private int focusWordInDocsSpam;
    private int focusWordInDocsNoSpam;


    /**
     * This constructor takes in a focusword, which gets counted by the WordCounter object.
     * To prevent confusion the focusword is colled wordOfInterest.
     *
     * @param focusWord The word that has to be counted
     */
    public WordCounter(String focusWord)
    {
        wordOfInterest = focusWord;
    }

    /**
     * Provides the focusword for the associated WordCounter object.
     *
     * @return the focusword
     */
    public String getFocusWord()
    {
        return wordOfInterest;
    }

    /**
     * This method extracts the following information from a classified document:
     * Is the document spam (document starts with a 1) or no spam (document starts wih a 0). I done by the first if statement which checks if the first character is a 0 or a 1.
     * It count the amount the focusword appears in the document. Every word from the document gets compared to the focusword
     * It counts the total word of the document, this is the total length of the document minus one, to correct for the 0/1 character
     *
     * @param document The input is a classified document
     */
    public void addSample(String document) {
        String[] words = document.split(" ");

        if (words[0].equals("0")) {
            totalNoSpamDocWords += words.length - 1;
            amountNoSpamDocs+=1;
            for (String word : words) {
                if (word.equals(wordOfInterest)) {
                    focusWordInDocsNoSpam += 1;
                }
            }
        } else {
            amountSpamDocs+=1;
            totalSpamDocWords += words.length - 1;
            for (String word : words) {
                if (word.equals(wordOfInterest)) {
                    focusWordInDocsSpam += 1;
                }
            }
        }
    }

    /**
     * This method check if the counter is trained, therefor it must have seen at least a spam file, a none-spam file and the focusword.
     *
     * @return true if counter is trained and return false if the counter in not trained.
     */
    public boolean isCounterTrained()
    {
        return amountSpamDocs>=1 && amountNoSpamDocs>=1 && (focusWordInDocsSpam>=1 || focusWordInDocsNoSpam>=1);
    }

    /**
     * This method calculates the probability that a random word is the focusword for no spam documents.
     *
     * @throws IllegalStateException if the counter is not yet trained. In that cae an initial estimate for spam/none-spam probability makes no sense.
     * @return probability that random word from all word of no spam documents is focusword
     */
    public double getConditionalNoSpam()
    {
        if(!isCounterTrained())
        {
            throw new IllegalStateException("Counter is not trained.");
        }
        return (double)focusWordInDocsNoSpam/(double)totalNoSpamDocWords;
    }

    /**
     * This method calculates the probability that a random word is the focusword for spam documents.
     *
     * @throws IllegalStateException if the counter is not yet trained. In that cae an initial estimate for spam/none-spam probability makes no sense.
     * @return probability that random word from all word of  spam documents is focusword
     */
    public double getConditionalSpam()
    {
        if(!isCounterTrained())
        {
            throw new IllegalStateException("Counter is not trained.");
        }
        return (double)focusWordInDocsSpam/(double)totalSpamDocWords;
    }
}



