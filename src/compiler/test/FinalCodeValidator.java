package compiler.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FinalCodeValidator {
	
	
	/**
	 * Searches for ENS files within the test folder
	 * @param dir test folder
	 * @param ignoredDir dir with the expected results (inside test folder), to ignore
	 * @param results list containing the .ENS files to run
	 */
	private static void searchEnsFiles(File dir, File ignoredDir, List<File> results) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                if (!f.equals(ignoredDir)) searchEnsFiles(f, ignoredDir, results);
            } else if (f.getName().endsWith(".ens")) {
                results.add(f);
            }
        }
    }
	
	/**
	 * Runs the selected ENS file and compares its output (prints) to the expected output.
	 * @param ensFile ENS file to run
	 * @param expectedDir directory where the expected outputs are
	 * @param ensJar jar for ens2025
	 * @return
	 */
	private static boolean validateFile(File ensFile, File expectedDir, File ensJar) {
        System.out.print("Test: "+ ensFile.getName() + " -> ");

        
        File expectedFile = new File(expectedDir, ensFile.getName() + ".out");

        if (!expectedFile.exists()) {
            System.out.println("[MISSING EXPECTED FILE] -> " + expectedFile.getName());
            return false;
        }

        try {
            // java -jar ens2025.jar -r file.ens
            ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", ensJar.getAbsolutePath(), "-r", ensFile.getAbsolutePath()
            );
            
            Process process = pb.start();
            
            
            String actualOutput = readStream(process.getInputStream()); //read stream
            consumeStream(process.getErrorStream());
            
            process.waitFor();

            String expectedContent = new String(Files.readAllBytes(expectedFile.toPath()));

            // Normalize to compare
            actualOutput = actualOutput.replace("\r\n", "\n").trim();
            expectedContent = expectedContent.replace("\r\n", "\n").trim();

            if (actualOutput.equals(expectedContent)) {
                System.out.println("[CORRECT]");
                return true;
            } else {
                System.out.println("[ERRROR] Differing outputs.");
                System.out.println("--- Expected ---\n" + expectedContent);
                System.out.println("--- Got ---\n" + actualOutput);
                return false;
            }

        } catch (Exception e) {
            System.out.println("[RUNTIME ERROR] " + e.getMessage());
            return false;
        }
    }
	
	/**
     * Reads data from the stream and returns it as String.
     * @param is InputStream to read
     * @return string from the stream
     * @throws IOException
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
	 * Finishes consuming the stream, so no data is left from one execution to another
	 * @param is InputStream to read
	 */
	private static void consumeStream(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {}
        } catch (IOException e) {}
    }
	
	/**
	 * Main method to evaluate ENS files
	 * @param nameFolder folder with test files
	 * @param ensJar location of ens2025
	 */
	public void testFinalCode(String nameFolder, String ensJar) {
		
        
        File rootDir = new File(nameFolder);       
        File ensFileJar = new File(ensJar);        
        
        File expectedDir = new File(rootDir, "expected");

        if (!expectedDir.exists()) {
            System.err.println("[ERROR]: The 'expected' folder does not exist in " + rootDir + ". The ENS code cannot be validated.");
            return;
        }

        System.out.println("Validating Execution with ENS 2025...");
        
        long startTime = System.currentTimeMillis();
        List<File> ensFiles = new ArrayList<>();
        searchEnsFiles(rootDir, expectedDir, ensFiles);

        if (ensFiles.isEmpty()) {
            System.out.println("There are no ENS files to validate.");
            return;
        }

        int passed = 0;
        for (File ensFile : ensFiles) {
            if (validateFile(ensFile, expectedDir, ensFileJar)) {
                passed++;
            }
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("--------------------------------------");
        System.out.println("ENS Execution Summary (" + duration + " ms)");
        System.out.println("--------------------------------------");
        System.out.println("Total Tests: " + ensFiles.size());
        System.out.println("As expected: " + passed);
        System.out.println("--------------------------------------");
        double percentage = (ensFiles.size() > 0) ? ((double)passed / ensFiles.size()) * 100 : 0;
        System.out.printf("Success Rate: %.2f%%\n", percentage);
		
	}
	
	/**
     * Starts the ENS validation test case.
     * @param args
     */
	public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Use: java FinalCodeValidator \"dir_test_valid\" \"dir_ens\"");
            return;
        }
        
        FinalCodeValidator finalCodeVal = new FinalCodeValidator ();
        finalCodeVal.testFinalCode(args[0], args[1]);
        
    }
}
