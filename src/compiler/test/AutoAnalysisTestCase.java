package compiler.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for automatic checking of the analysis phase (lexical, syntax, and semantic).
 */
public class AutoAnalysisTestCase {
	
	/**
     * Test category. Logs the folder where tests are located, and the expected result of compiling them
     */
    private class TestCategory {
        String folderName;
        CompilerResult expectedResult;

        TestCategory(String folder, CompilerResult expected) {
            this.folderName = folder;
            this.expectedResult = expected;
        }
    }
	
	public enum CompilerResult {
		VALID, 
        LEXICAL_ERROR, 
        SYNTAX_ERROR, 
        SEMANTIC_ERROR
    }
	
	private final File baseDir;
    private final List<TestCategory> categories;
    
    private static String sourceFileExtension = FinalTestCase.sourceFileExtension;
    
    // Global counters for the errors
    private int totalTests = 0;
    private int successCount = 0;
    private int zealousCount = 0;
    private int lenientCount = 0;
    
    // Confusion matrix for errors in unexpected categories
    private int lexInSyntax = 0, lexInSemantic = 0;
    private int syntaxInLex = 0, syntaxInSemantic = 0;
    private int semanticInLex = 0, semanticInSyntax = 0;
	
	/**
     * Constructor for AutoAnalysisTestCase
     */
    public AutoAnalysisTestCase (String testPath)
    {
        this.baseDir = new File(testPath);
        this.categories = new ArrayList<>();
        
        // Register expected error categories
        registerCategory("valid", CompilerResult.VALID);
        registerCategory("lexical", CompilerResult.LEXICAL_ERROR);
        registerCategory("syntax", CompilerResult.SYNTAX_ERROR);
        registerCategory("semantic", CompilerResult.SEMANTIC_ERROR);
    }
	
    /**
     * Register a new category error with its folder and its expected result
     */
    public void registerCategory(String folderName, CompilerResult expectedResult) {
        this.categories.add(new TestCategory(folderName, expectedResult));
    }
    
    /**
     * Main method to run the test suite
     */
    public void run() {
        if (!baseDir.exists()) {
            System.err.println("[ERROR]: Test directory does not exist: " + baseDir.getAbsolutePath());
            return;
        }

        System.out.println("Starting Test Suite...");
        System.out.println("Base Test Directory: " + baseDir.getAbsolutePath());

        long startTime = System.currentTimeMillis();

        // For each category, run its tests
        for (TestCategory cat : categories) {
            runCategory(cat);
        }

        long duration = System.currentTimeMillis() - startTime;
        printSummary(duration);
    }
    
    /**
     * Run a test category (
     * @param category
     */
    private void runCategory(TestCategory category) {
    	
        File folder = new File(baseDir, category.folderName);
        if (!folder.exists()) {
        	System.out.println("There is no folder in: "+folder.getPath());
        	return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(sourceFileExtension)); 
        if (files == null) return;

        for (File file : files) {
            totalTests++;
            executeSingleTest(file, category.expectedResult);
        }
    }
    
    
    /**
     * Runs an individual test with FinalTestCase.
     * @param file the file to compile
     * @param expected the expected result
     */
    private void executeSingleTest(File file, CompilerResult expected) {
    	System.out.print("Test: " + file.getParentFile().getName() + "/" + file.getName() + " -> ");

        String output = "";
        
        try {
            // java -jar classpath FinalTestCase file
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path");
            String className = "compiler.test.FinalTestCase";

            ProcessBuilder pb = new ProcessBuilder(
                    javaBin, "-cp", classpath, className, file.getAbsolutePath()
            );

            Process process = pb.start();

            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());

            process.waitFor();

            output = stdout + "\n" + stderr;
        } catch (Exception e) {
            e.printStackTrace(); // Unexpected error
            output = "EXCEPTION: " + e.getMessage();
        }

        // parsing the possible errors
        CompilerResult actual = CompilerResult.VALID;
        String outputUpper = output.toUpperCase();

        if (outputUpper.contains("LEXICAL ERROR") || outputUpper.contains("LEXICAL FATAL")) actual = CompilerResult.LEXICAL_ERROR;
        else if (outputUpper.contains("SYNTAX ERROR") || outputUpper.contains("SYNTAX FATAL")) actual = CompilerResult.SYNTAX_ERROR;
        else if (outputUpper.contains("SEMANTIC ERROR") || outputUpper.contains("SEMANTIC FATAL")) actual = CompilerResult.SEMANTIC_ERROR;

        evaluate(expected, actual);
    }
    
    /**
     * Reads data from the stream and returns it as String.
     * @param is InputStream to read
     * @return string from the stream
     * @throws IOException if there are I/O errors
     */
	private static String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toString("UTF-8"); 
    }
    
    /**
     * Evaluates the expected vs actual result and logs it.
     * @param expected
     * @param actual
     */
    private void evaluate(CompilerResult expected, CompilerResult actual) {
        if (expected == actual) {
            System.out.println("[CORRECT]");
            successCount++;
            return;
        }

        // If expected is VALID, but it failed: too strict
        if (expected == CompilerResult.VALID) {
            System.out.println("[ERROR]: Too strict. Expected VALID, got " + actual);
            zealousCount++;
            return;
        }

        // If expected is fail, but it is VALID: too lenient
        if (actual == CompilerResult.VALID) {
            System.out.println("[ERROR]: Too Lenient. Expected " + expected + " error, but it was not detected.");
            lenientCount++;
            return;
        }

        // Crossed errors (ie expected lexical but got semantic)
        System.out.println("[ERROR]: Unexpected error type. Expected " + expected + ", got " + actual);
        recordCrossError(expected, actual);
    }
    
    /**
     * To record unexpected errors
     * @param expected error that was expected
     * @param actual error produced
     */
    private void recordCrossError(CompilerResult expected, CompilerResult actual) {
        if (expected == CompilerResult.LEXICAL_ERROR) {
            if (actual == CompilerResult.SYNTAX_ERROR) lexInSyntax++;
            if (actual == CompilerResult.SEMANTIC_ERROR) lexInSemantic++;
        } else if (expected == CompilerResult.SYNTAX_ERROR) {
            if (actual == CompilerResult.LEXICAL_ERROR) syntaxInLex++;
            if (actual == CompilerResult.SEMANTIC_ERROR) syntaxInSemantic++;
        } else if (expected == CompilerResult.SEMANTIC_ERROR) {
            if (actual == CompilerResult.LEXICAL_ERROR) semanticInLex++;
            if (actual == CompilerResult.SYNTAX_ERROR) semanticInSyntax++;
        }
    }
    
    /**
     * Print error summary
     * @param duration
     */
    private void printSummary(long duration) {
        System.out.println("\n--------------------------------------");
        System.out.println("Execution Summary (" + duration + " ms)");
        System.out.println("--------------------------------------");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("As expected: " + successCount);
        
        if (zealousCount > 0) System.out.println("Too strict: " + zealousCount);
        if (lenientCount > 0) System.out.println("Too lenient: " + lenientCount);
        
        printCrossError("Lexical error detected as Syntactic", lexInSyntax);
        printCrossError("Lexical error detected as Semantic", lexInSemantic);
        printCrossError("Syntactic error detected as Lexical", syntaxInLex);
        printCrossError("Syntactic error detected as Semantic", syntaxInSemantic);
        printCrossError("Semantic error detected as Lexical", semanticInLex);
        printCrossError("Semantic error detected as Syntactic", semanticInSyntax);
        
        System.out.println("--------------------------------------");
        double percentage = (totalTests > 0) ? ((double)successCount / totalTests) * 100 : 0;
        System.out.printf("Success Rate: %.2f%%\n", percentage);
    }
    
    private void printCrossError(String msg, int count) {
        if (count > 0) System.out.println(msg + ": " + count);
    }
    
	/**
     * Starts the AutomatedTestCase.
     * @param args
     */
    public static void main (String args[])
    {  
        if (args.length < 1 || args.length > 1) 
        {
        	System.err.println ("Use: java AutoAnalysisTestCase \"testDir\"");        
        }
        else
        {
        	
        	File root = new File (args[0]);
            if (!root.exists ()) {
                System.err.println ("[ERROR]: Test Directory does not exist: " + root.getAbsolutePath());
                return;
            }
            
            AutoAnalysisTestCase runner = new AutoAnalysisTestCase(args[0]);
            runner.run();
        }
    }	
    
    
    
}
