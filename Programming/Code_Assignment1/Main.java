import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        //WordCounter wc = new WordCounter("good"); //Call on constructor

        //System.out.println(wc.document);
        //System.out.println(wc.getFocusWord());
        //wc.addSample("1 good bad bad bad");
        //wc.addSample("0 bad good good");
        //wc.addSample("0 bad good");
        //wc.getWcStats();
        //wc.isCounterTrained();
        //System.out.println(wc.getConditionalNoSpam());
        String [] words= {"good", "bad"};
       // wc.Test(words);

        //More test on Naive Bayes
        NaiveBayes nb = new NaiveBayes(words);
        /*nb.addSample("1 good bad bad bad casino");
        nb.addSample("0 bad good good pizza");
        nb.addSample("0 bad good tapas");*/
        File f = new File("/Users/wesse/IdeaProjects/Assignment23/src/traindata.txt");
        nb.trainClassifier(f);
        //nb.test(new File("/Users/wesse/IdeaProjects/Assignment23/src/testdata.txt"));
        //nb.classifyFile(new File("/Users/wesse/IdeaProjects/Assignment23/src/newdata.txt"), new File("/Users/wesse/IdeaProjects/Assignment23/src/classifications.txt"));
        ConfusionMatrix cm = nb.computeAccuracy(new File("/Users/wesse/IdeaProjects/Assignment23/src/testdata.txt"));
        System.out.println(cm.getTruePositives());
        System.out.println(cm.getFalsePositives());
        System.out.println(cm.getTrueNegatives());
        System.out.println(cm.getFalseNegatives());
        /*System.out.println(nb.classify("good"));
        System.out.println(nb.classify("bad"));
        System.out.println(nb.classify("good bad bad"));
        System.out.println(nb.classify("pizza"));
        System.out.println(nb.classify("casino"));*/

    }



}
