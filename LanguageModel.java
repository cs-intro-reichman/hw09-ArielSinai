import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model.
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        // Your code goes here

        In in = new In(fileName);
        String window = "";
        char chr;

        for (int i = 0; i < windowLength; i++) {
            window += in.readChar();
        }

        while (in.isEmpty() == false) {
            chr = in.readChar();
            List probs = CharDataMap.get(window);

            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }

            window += chr;
            window = window.substring(1);
            probs.update(chr);
        }

        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        // Your code goes here

        int count = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            int currentCpCount = probs.listIterator(i).current.cp.count;
            count += currentCpCount;
        }

        for (int i = 0; i < probs.getSize(); i++) {
            CharData prev;

            if (i > 0) {
                prev = probs.listIterator(i - 1).current.cp;
            } else {
                prev = null;
            }

            CharData curr = probs.listIterator(i).current.cp;
            curr.p = ((double) curr.count) / ((double) count);

            if (i > 0) {
                curr.cp = prev.cp + curr.p;
            } else {
                curr.cp = curr.p;
            }
        }

    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        // Your code goes here
        int i = 0;
        double rg = randomGenerator.nextDouble();

        while (probs.listIterator(i).current.cp.cp < rg) {
            i++;
        }

        char randomChar = probs.get(i).chr;
        return randomChar;
    }

    /**
     * Generates a random text, based on the probabilities that were learned during
     * training.
     * 
     * @param initialText     - text to start with. If initialText's last substring
     *                        of size numberOfLetters
     *                        doesn't appear as a key in Map, we generate no text
     *                        and return only the initial text.
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        // Your code goes here
        if (initialText.length() < windowLength) {
            return initialText;
        }

        List last = CharDataMap.get(initialText.substring(initialText.length() - windowLength));
        if (last == null) {
            return initialText;
        }

        String window = initialText.substring(initialText.length() - windowLength);
        String result = window;
        while (result.length() < (textLength + windowLength)) {
            List probs = CharDataMap.get(window);
            if (probs == null) {
                return result;
            }
            result += getRandomChar(probs);
            window = result.substring(result.length() - windowLength);
        }

        return result;
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        // Your code goes here
    }
}
