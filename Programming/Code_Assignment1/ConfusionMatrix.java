/**
 * This class is a container for the accuracy of the NaiveBayes classifier
 * @author 589273wb Wessel Boosman
 */

public class ConfusionMatrix {
    private int truePos;
    private int trueNeg;
    private int falsePos;
    private int falseNeg;
    /**
     * This method returns the incremented values of true positives.
     * @return True positive plus one
     */
    public int incrementTruePos()
    {
        return truePos++;
    }
    /**
     * This method returns the incremented values of true negatives.
     * @return True negative plus one
     */
    public int incrementTrueNeg()
    {
        return trueNeg++;
    }
    /**
     * This method returns the incremented values of false positives.
     * @return false positive plus one
     */
    public int incrementFalsePos()
    {
        return falsePos++;
    }

    /**
     * This method returns the incremented values of false negatives.
     * @return false negative plus one
     */
    public int incrementFalseNeg()
    {
        return falseNeg++;
    }

    /**
     * This method returns the true negatives classifications from the testdata in order to get insight in the accuracy of the classifier.
     * @return True negative classified files
     */
    public int getTrueNegatives()
    {
        return trueNeg;
    }

    /**
     * This method returns the true positives classifications from the testdata in order to get insight in the accuracy of the classifier.
     * @return True positives classified files
     */
    public int getTruePositives()
    {
        return truePos;
    }

    /**
     * This method returns the false negatives classifications from the testdata in order to get insight in the accuracy of the classifier.
     * @return False negative classified files
     */
    public int getFalseNegatives()
    {
        return falseNeg;
    }

    /**
     * This method returns the false positives classifications from the testdata in order to get insight in the accuracy of the classifier.
     * @return False positives classified files
     */
    public int getFalsePositives()
    {
        return falsePos;
    }
}
